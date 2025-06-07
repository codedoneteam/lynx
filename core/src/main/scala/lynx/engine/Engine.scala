package lynx.engine

import cats.implicits.*
import lynx.types.*
import cats.Monad
import cats.data.StateT
import lynx.engine.firer.Firer
import lynx.rules.*

trait Engine[F[_], A]:
  def run(rules: Rules[F, A], tail: Rules[F, A], facts: Facts[A], n: Int, max: Int): StateT[F, Session[F, A], Facts[A]]

object Engine:
  def apply[F[_], A](using engine: Engine[F, A]): Engine[F, A] = engine

  given [F[_] : Monad, A](using
    firer1: Firer[F, A, A, Rule1[F, A]],
    firer2: Firer[F, A, (A, A), Rule2[F, A]],
    firerN: Firer[F, A, Set[A], RuleN[F, A]]
  ): Engine[F, A] with
      override def run(rules: Rules[F, A], tail: Rules[F, A], facts: Facts[A], n: Int, max: Int): StateT[F, Session[F, A], Facts[A]] =
        for
          fired <-
            tail.headOption
              .traverse {
                case rule1 @ Rule1(_, _) => Firer[F, A, A, Rule1[F, A]].fire(rule1, facts, n, max)
                case rule2 @ Rule2(_, _) => Firer[F, A, (A, A), Rule2[F, A]].fire(rule2, facts, n, max)
                case ruleN @ RuleN(_, _) => Firer[F, A, Set[A], RuleN[F, A]].fire(ruleN, facts, n, max)
              }

          session <- fired.fold(StateT.liftF[F, Session[F, A], Facts[A]](facts.pure[F])) { mutated =>
            if (mutated eq facts)
              run(rules, tail.tail, facts, n + 1, max)
            else
              run(rules, rules, mutated, n + 1, max)
          }
        yield session