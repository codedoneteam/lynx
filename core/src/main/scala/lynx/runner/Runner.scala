package lynx.runner

import cats.implicits.*
import cats.MonadThrow
import cats.data.EitherT
import lynx.engine.Engine
import lynx.engine.firer.Firer
import lynx.engine.firer.exception.{ApplyException, FiringException, MaxTimesApplyException}
import lynx.rules.{Rule1, Rule2, RuleN}
import lynx.types.{Facts, Rules, Session}
import org.typelevel.log4cats.Logger

trait Runner[F[_], A]:
  def run(max: Int)(rules: Rules[F, A], facts: Facts[A]): EitherT[F, FiringException, Facts[A]]

object Runner:
  def apply[F[_], A](using runner: Runner[F, A]): Runner[F, A] = runner

  given [F[_] : MonadThrow : Logger, A](using
    firer1: Firer[F, A, A, Rule1[F, A]],
    firer2: Firer[F, A, (A, A), Rule2[F, A]],
    firerN: Firer[F, A, Set[A], RuleN[F, A]]
  ): Runner[F, A] with
      override def run(max: Int)(rules: Rules[F, A], facts: Facts[A]): EitherT[F, FiringException, Facts[A]] =
        Engine[F, A]
          .run(rules, rules, facts, 0, max)
          .run(createSession)
          .map {
            case (_, mutated) => mutated
          }
          .attemptT
          .leftMap[FiringException] {
            case e: MaxTimesApplyException => e
            case e                         => ApplyException(e)
    }

  private[runner] def createSession[F[_], A]: Session[F, A] = Set.empty