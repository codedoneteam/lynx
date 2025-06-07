package lynx.runner

import cats.Applicative
import cats.data.StateT
import cats.effect.{IO, Sync}
import cats.implicits.*
import lynx.engine.firer.Firer
import lynx.rules.*
import lynx.test.gens.Rule1Gens.given
import lynx.test.TestFact
import lynx.test.TestFact.*
import lynx.types.Facts
import munit.CatsEffectSuite
import org.scalacheck.Arbitrary.arbitrary
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import RunnerSpec.given

class RunnerSpec extends CatsEffectSuite:
  test("Runner") {
    arbitrary[Rule1[IO, TestFact]]
      .map(rule1 => Runner[IO, TestFact].run(1)(Set(rule1), Set(TestFact1(0))))
      .sample
      .get
      .rethrowT
      .assertEquals(Set[TestFact](TestFact1(0)))
  }

object RunnerSpec:
  given [F[_] : Sync]: Logger[F] = Slf4jLogger.getLoggerFromName("Logger")

  given [F[_] : Applicative]: Firer[F, TestFact, TestFact, Rule1[F, TestFact]] =
    (_: Rule1[F, TestFact], facts: Facts[TestFact], _: Int, _: Int) => StateT.liftF(facts.pure[F])

  given [F[_] : Applicative]: Firer[F, TestFact, (TestFact, TestFact), Rule2[F, TestFact]] =
    (_: Rule2[F, TestFact], _: Facts[TestFact], _: Int, _: Int) => StateT.liftF(Set[TestFact](TestFact1(1)).pure[F])

  given [F[_] : Applicative]: Firer[F, TestFact, Set[TestFact], RuleN[F, TestFact]] =
    (_: RuleN[F, TestFact], _: Facts[TestFact], _: Int, _: Int) => StateT.liftF(Set[TestFact](TestFact1(1)).pure[F])
