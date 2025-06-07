package lynx.engine

import cats.implicits.*
import cats.effect.{IO, Sync}
import lynx.engine.firer.Firer
import lynx.rules.*
import lynx.test.gens.Rule1Gens.given
import lynx.test.TestFact
import lynx.test.TestFact.*
import munit.CatsEffectSuite
import org.scalacheck.Arbitrary.arbitrary
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import EngineSpec.given
import cats.Applicative
import cats.data.StateT
import lynx.types.*

class EngineSpec extends CatsEffectSuite:
  test("Run engine") {
    arbitrary[Rule1[IO, TestFact]]
      .map(rule1 => Engine[IO, TestFact].run(Set(rule1), Set(rule1), Set(TestFact1(0)), 0, 1).run(Set.empty))
      .sample
      .get
      .map { case (_, facts) => facts }
      .assertEquals(Set[TestFact](TestFact1(0)))
  }

object EngineSpec:
  given [F[_] : Sync]: Logger[F] = Slf4jLogger.getLoggerFromName("Logger")

  given [F[_] : Applicative]: Firer[F, TestFact, TestFact, Rule1[F, TestFact]] =
    (_: Rule1[F, TestFact], facts: Facts[TestFact], _: Int, _: Int) => StateT.liftF(facts.pure[F])

  given [F[_] : Applicative]: Firer[F, TestFact, (TestFact, TestFact), Rule2[F, TestFact]] =
    (_: Rule2[F, TestFact], _: Facts[TestFact], _: Int, _: Int) => StateT.liftF(Set[TestFact](TestFact1(1)).pure[F])

  given [F[_] : Applicative]: Firer[F, TestFact, Set[TestFact], RuleN[F, TestFact]] =
    (_: RuleN[F, TestFact], _: Facts[TestFact], _: Int, _: Int) => StateT.liftF(Set[TestFact](TestFact1(1)).pure[F])