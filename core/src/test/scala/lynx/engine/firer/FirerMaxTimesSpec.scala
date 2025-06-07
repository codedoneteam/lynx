package lynx.engine.firer

import cats.Applicative
import cats.effect.{IO, Sync}
import lynx.engine.firer.FirerSpec.*
import lynx.engine.firer.apply.Apply
import lynx.engine.firer.exception.MaxTimesApplyException
import lynx.engine.firer.state.State
import lynx.engine.firer.supplier.Supplier
import lynx.rules.*
import lynx.test.gens.RuleGens.given
import lynx.test.TestFact
import lynx.test.TestFact.*
import lynx.types.{Facts, Session}
import munit.CatsEffectSuite
import org.scalacheck.Arbitrary.arbitrary
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import FirerMaxTimesSpec.given

class FirerMaxTimesSpec extends CatsEffectSuite:
  test("Fire max times") {
    arbitrary[Rule1[IO, TestFact]]
      .map(rule1 => Firer[IO, TestFact, TestFact, Rule1[IO, TestFact]].fire(rule1, Set(TestFact1(0)), 0, 1).run(Set.empty))
      .sample
      .get
      .map(_ => false)
      .handleError { case MaxTimesApplyException(_) => true }
      .assertEquals(true)
  }

object FirerMaxTimesSpec:
  given [F[_] : Sync]: Logger[F] = Slf4jLogger.getLoggerFromName("Logger")

  given [F[_] : Applicative]: State[F, TestFact, TestFact, Rule1[F, TestFact]] with
      override def enrich(rule: Rule1[F, TestFact], session: Session[F, TestFact], input: TestFact): Session[F, TestFact] = session
      
      override def has(rule: Rule1[F, TestFact], session: Session[F, TestFact], input: TestFact): Boolean = false

  given [F[_]]: Apply[F, TestFact, TestFact, Rule1[F, TestFact]] = (rule: Rule1[F, TestFact]) => rule.apply

  given [F[_]]: Supplier[F, TestFact, TestFact] = (facts: Facts[TestFact]) => facts