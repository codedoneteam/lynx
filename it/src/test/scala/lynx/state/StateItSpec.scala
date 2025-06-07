package lynx.state

import lynx.all.*
import StateItSpec.*
import StateItSpec.given
import cats.effect.{IO, Sync}
import cats.effect.kernel.{Clock, Temporal}
import cats.{Applicative, Functor}
import lynx.state.domain.State
import lynx.state.domain.State.Start
import lynx.state.domain.State.End
import lynx.state.rules.StartRule.startRule
import lynx.state.rules.WaitRule.waitRule
import lynx.types.Rules
import munit.CatsEffectSuite
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class StateItSpec extends CatsEffectSuite:
  test("State") {
    rules[IO]
      .fire()(Start)
      .rethrowT
      .assertEquals(Set[State](End))
  }

object StateItSpec:
  given [F[_] : Sync]: Logger[F] = Slf4jLogger.getLoggerFromName("Logger")

  def rules[F[_] : Applicative : Functor : Temporal : Clock]: Rules[F, State] = startRule[F] <+> waitRule[F]