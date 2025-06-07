package lynx.engine.firer

import cats.MonadThrow
import cats.data.StateT
import cats.implicits.*
import lynx.engine.firer.apply.Apply
import lynx.engine.firer.exception.MaxTimesApplyException
import lynx.engine.firer.state.State
import lynx.engine.firer.supplier.Supplier
import lynx.rules.*
import lynx.types.*
import org.typelevel.log4cats.Logger

trait Firer[F[_], A, I, R <: Rule[F, A]]:
  def fire(rule: R, facts: Facts[A], n: Int, max: Int): StateT[F, Session[F, A], Facts[A]]

object Firer:
  def apply[F[_], A, I, R <: Rule[F, A]](using firer: Firer[F, A, I, R]): Firer[F, A, I, R] = firer

  given [F[_] : MonadThrow : Logger, A <: AnyRef, I, R <: Rule[F, A]](using
    stateF: State[F, A, I, R],
    applyF: Apply[F, A, I, R],
    supplierF: Supplier[F, A, I]
  ): Firer[F, A, I, R] with
      override def fire(rule: R, facts: Facts[A], n: Int, max: Int): StateT[F, Session[F, A], Facts[A]] =
        StateT { session =>
          Supplier[F, A, I]
            .supply(facts)
            .find(input => Apply[F, A, I, R].applyFunction(rule).isDefinedAt(input) && !State[F, A, I, R].has(rule, session, input))
            .traverse { input =>
              for
                mutator <-
                  Apply[F, A, I, R]
                    .applyFunction(rule)(input)
                    .onError(e => Logger[F].trace(e)(s"Something happened while firing rule ${rule.name}. ${e.getMessage}"))

                mutated = mutator(facts)

                firedSession = State[F, A, I, R].enrich(rule, session, input)

                _ <- Logger[F].trace(s"Fired ${rule.name} on $input")
                result <-
                  (n < max)
                    .pure[F]
                    .ifM(fire(rule, mutated, n + 1, max).run(firedSession), MonadThrow[F].raiseError(MaxTimesApplyException(max)))
              yield result
            }
            .map(_.getOrElse(session -> facts))
        }