package lynx.engine.firer.state

import cats.Id
import lynx.engine.firer.state.StateN.given
import lynx.rules.*
import lynx.test.gens.RuleNGens.given
import lynx.test.gens.TestFactsGens.given
import lynx.test.TestFact
import lynx.test.TestFact.*
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class StateNSpec extends AnyFunSuite with Matchers:
  test("StateN enrich session") {
    arbitrary[RuleN[Id, TestFact]]
      .map(rule => State[Id, TestFact, Set[TestFact], RuleN[Id, TestFact]].enrich(rule, Set.empty, Set(TestFact1(0))))
      .map(_.size)
      .sample
      .get
      .shouldBe(1)
  }

  test("StateN enrich session by same fact") {
    arbitrary[RuleN[Id, TestFact]]
      .map(rule => rule -> State[Id, TestFact, Set[TestFact], RuleN[Id, TestFact]].enrich(rule, Set.empty, Set(TestFact1(0))))
      .map { case (rule, session) => State[Id, TestFact, Set[TestFact], RuleN[Id, TestFact]].enrich(rule, session, Set(TestFact1(0))) }
      .map(_.size)
      .sample
      .get
      .shouldBe(1)
  }

  test("StateN enrich session by new fact") {
    arbitrary[RuleN[Id, TestFact]]
      .map(rule => rule -> State[Id, TestFact, Set[TestFact], RuleN[Id, TestFact]].enrich(rule, Set.empty, Set(TestFact1(0))))
      .map { case (rule, session) => State[Id, TestFact, Set[TestFact], RuleN[Id, TestFact]].enrich(rule, session, Set(TestFact1(1))) }
      .map(_.size)
      .sample
      .get
      .shouldBe(1)
  }

  test("Stet1 enrich same rule with same fact") {
    arbitrary[TestFact1]
      .map(Set[TestFact](_))
      .flatMap(
        test =>
          arbitrary[RuleN[Id, TestFact]]
            .map(rule => (rule, test, State[Id, TestFact, Set[TestFact], RuleN[Id, TestFact]].enrich(rule, Set.empty, test)))
            .map {
              case (rule, testFact1, session) => State[Id, TestFact, Set[TestFact], RuleN[Id, TestFact]].enrich(rule, session, testFact1)
            }
            .map(_.size)
            .sample
            .get
            .shouldBe(1)
      )
  }

  test("StateN enrich has applied fact") {
    arbitrary[TestFact1]
      .map(Set[TestFact](_))
      .flatMap(
        test =>
          arbitrary[RuleN[Id, TestFact]]
            .map(rule => (rule, State[Id, TestFact, Set[TestFact], RuleN[Id, TestFact]].enrich(rule, Set.empty, test)))
            .map {
              case (rule, session) => State[Id, TestFact, Set[TestFact], RuleN[Id, TestFact]].has(rule, session, test)
            }
            .sample
            .get
            .shouldBe(true)
      )
  }

  test("StateN enrich hasn't applied fact") {
    arbitrary[TestFact1]
      .map(_.copy(x = 0))
      .map(Set[TestFact](_))
      .flatMap(
        test =>
          arbitrary[RuleN[Id, TestFact]]
            .map(rule => (rule, State[Id, TestFact, Set[TestFact], RuleN[Id, TestFact]].enrich(rule, Set.empty, test)))
            .map { case (rule, session) => State[Id, TestFact, Set[TestFact], RuleN[Id, TestFact]].has(rule, session, Set(TestFact1(0))) }
            .sample
            .get
            .shouldBe(false)
      )
  }