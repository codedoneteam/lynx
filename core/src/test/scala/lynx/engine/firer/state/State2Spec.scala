package lynx.engine.firer.state

import cats.Id
import lynx.engine.firer.state.State2.given
import lynx.rules.*
import lynx.test.gens.Rule2Gens.given
import lynx.test.gens.TestFactsGens.given 
import lynx.test.TestFact
import lynx.test.TestFact.*
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class State2Spec extends AnyFunSuite with Matchers:
  test("Rule2 enrich session") {
    arbitrary[Rule2[Id, TestFact]]
      .map(rule => State[Id, TestFact, (TestFact, TestFact), Rule2[Id, TestFact]].enrich(rule, Set.empty, TestFact1(0) -> TestFact2(0)))
      .map(_.size)
      .sample
      .get
      .shouldBe(1)
  }

  test("Rule2 enrich session by same fact") {
    arbitrary[Rule2[Id, TestFact]]
      .map(
        rule => rule -> State[Id, TestFact, (TestFact, TestFact), Rule2[Id, TestFact]].enrich(rule, Set.empty, TestFact1(0) -> TestFact2(0))
      )
      .map {
        case (rule, session) =>
          State[Id, TestFact, (TestFact, TestFact), Rule2[Id, TestFact]].enrich(rule, session, TestFact1(0) -> TestFact2(0))
      }
      .map(_.size)
      .sample
      .get
      .shouldBe(1)
  }

  test("Rule2 enrich session by new fact") {
    arbitrary[Rule2[Id, TestFact]]
      .map(
        rule => rule -> State[Id, TestFact, (TestFact, TestFact), Rule2[Id, TestFact]].enrich(rule, Set.empty, TestFact1(0) -> TestFact2(0))
      )
      .map {
        case (rule, session) =>
          State[Id, TestFact, (TestFact, TestFact), Rule2[Id, TestFact]].enrich(rule, session, TestFact1(1) -> TestFact2(0))
      }
      .map(_.size)
      .sample
      .get
      .shouldBe(2)
  }

  test("Stet1 enrich same rule with same fact") {
    arbitrary[TestFact1]
      .flatMap(
        testFact1 =>
          arbitrary[Rule2[Id, TestFact]]
            .map(
              rule => (rule, State[Id, TestFact, (TestFact, TestFact), Rule2[Id, TestFact]].enrich(rule, Set.empty, testFact1 -> testFact1))
            )
            .map {
              case (rule, session) =>
                State[Id, TestFact, (TestFact, TestFact), Rule2[Id, TestFact]].enrich(rule, session, testFact1 -> testFact1)
            }
            .map(_.size)
            .sample
            .get
            .shouldBe(1)
      )
  }

  test("Rule2 enrich has applied fact") {
    arbitrary[TestFact1]
      .map(testFact1 => testFact1 -> testFact1)
      .flatMap(
        test =>
          arbitrary[Rule2[Id, TestFact]]
            .map(rule => (rule, State[Id, TestFact, (TestFact, TestFact), Rule2[Id, TestFact]].enrich(rule, Set.empty, test)))
            .map { case (rule, session) => State[Id, TestFact, (TestFact, TestFact), Rule2[Id, TestFact]].has(rule, session, test) }
            .sample
            .get
            .shouldBe(true)
      )
  }

  test("Rule2 enrich hasn't applied fact") {
    arbitrary[TestFact1]
      .map(_.copy(x = 0))
      .map(testFact1 => testFact1 -> testFact1)
      .flatMap(
        test =>
          arbitrary[Rule2[Id, TestFact]]
            .map(rule => (rule, State[Id, TestFact, (TestFact, TestFact), Rule2[Id, TestFact]].enrich(rule, Set.empty, test)))
            .map {
              case (rule, session) =>
                State[Id, TestFact, (TestFact, TestFact), Rule2[Id, TestFact]].has(rule, session, TestFact1(1) -> TestFact1(1))
            }
            .sample
            .get
            .shouldBe(false)
      )
  }
