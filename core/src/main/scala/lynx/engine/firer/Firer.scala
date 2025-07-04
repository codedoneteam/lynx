package lynx.engine.firer

import cats.{MonadThrow, Show}
import lynx.session.SessionMonoid.sessionMonoid
import cats.effect.Ref
import cats.implicits._
import lynx.engine.firer.apply.Apply
import lynx.engine.firer.exception.MaxTimesApplyException
import lynx.engine.firer.state.State
import lynx.engine.firer.supplier.Supplier
import lynx.tagged._
import lynx.session.Session
import lynx.types._

trait Firer[F[_], A, I, R <: Tagged[F, A], P] {
  def fire(ref: Ref[F, Session[F, A]], pid: P, tagged: R, facts: Facts[A], n: Int, max: Int): F[Facts[A]]
}

object Firer {
  def apply[F[_], A, I, R <: Tagged[F, A], P](implicit firer: Firer[F, A, I, R, P]): Firer[F, A, I, R, P] = firer

  implicit def makeFirer[F[_] : MonadThrow, A <: AnyRef, I, R <: Tagged[F, A], P : Show](implicit
    stateF: State[F, A, I, R],
    applyF: Apply[F, A, I, R],
    supplierF: Supplier[F, A, I]
  ): Firer[F, A, I, R, P] =
    new Firer[F, A, I, R, P] {
      override def fire(sessionRef: Ref[F, Session[F, A]], pid: P, tagged: R, facts: Facts[A], n: Int, max: Int): F[Facts[A]] =
        sessionRef.get.flatMap(
          session =>
            Supplier[F, A, I]
              .supply(facts)
              .find(input => Apply[F, A, I, R].applyFunction(tagged).isDefinedAt(input) && !State[F, A, I, R].has(tagged, session, input))
              .traverse { input =>
                sessionRef
                  .getAndUpdate(_ |+| State[F, A, I, R].enrich(tagged, session, input))
                  .flatMap(
                    current =>
                      State[F, A, I, R]
                        .has(tagged, current, input)
                        .pure[F]
                        .ifM(
                          fire(sessionRef, pid, tagged, facts, n + 1, max),
                          for {
                            mutator <- Apply[F, A, I, R].applyFunction(tagged)(input)

                            mutated = mutator(facts)

                            result <-
                              (n < max)
                                .pure[F]
                                .ifM(
                                  fire(sessionRef, pid, tagged, mutated, n + 1, max),
                                  MonadThrow[F].raiseError(MaxTimesApplyException(max))
                                )
                          } yield result
                        )
                  )
              }
              .map(_.getOrElse(facts))
        )
    }
}
