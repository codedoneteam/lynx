package lynx.engine.firer.state

import cats.Id
import lynx.rules.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import lynx.engine.firer.state.State1.given
import lynx.test.TestFact
import lynx.test.gens.Rule1Gens.given
import lynx.test.gens.TestFactsGens.given
import lynx.test.TestFact.*
import org.scalacheck.Arbitrary.arbitrary

class State1Spec extends AnyFunSuite with Matchers:
  test("State1 enrich session") {
    arbitrary[Rule1[Id, TestFact]]
      .map(rule => State[Id, TestFact, TestFact, Rule1[Id, TestFact]].enrich(rule, Set.empty, TestFact1(0)))
      .map(_.size)
      .sample
      .get
      .shouldBe(1)
  }

  test("State1 enrich session by same fact") {
    arbitrary[Rule1[Id, TestFact]]
      .map(rule => rule -> State[Id, TestFact, TestFact, Rule1[Id, TestFact]].enrich(rule, Set.empty, TestFact1(0)))
      .map { case (rule, session) => State[Id, TestFact, TestFact, Rule1[Id, TestFact]].enrich(rule, session, TestFact1(0)) }
      .map(_.size)
      .sample
      .get
      .shouldBe(1)
  }

  test("State1 enrich session by new fact") {
    arbitrary[Rule1[Id, TestFact]]
      .map(rule => rule -> State[Id, TestFact, TestFact, Rule1[Id, TestFact]].enrich(rule, Set.empty, TestFact1(0)))
      .map { case (rule, session) => State[Id, TestFact, TestFact, Rule1[Id, TestFact]].enrich(rule, session, TestFact1(1)) }
      .map(_.size)
      .sample
      .get
      .shouldBe(2)
  }

  test("Stet1 enrich same rule with same fact") {
    arbitrary[TestFact1]
      .flatMap(
        testFact1 =>
          arbitrary[Rule1[Id, TestFact]]
            .map(rule => (rule, State[Id, TestFact, TestFact, Rule1[Id, TestFact]].enrich(rule, Set.empty, testFact1)))
            .map { case (rule, session) => State[Id, TestFact, TestFact, Rule1[Id, TestFact]].enrich(rule, session, testFact1) }
            .map(_.size)
            .sample
            .get
            .shouldBe(1)
      )
  }

  test("State1 enrich has applied fact") {
    arbitrary[TestFact1]
      .flatMap(
        testFact1 =>
          arbitrary[Rule1[Id, TestFact]]
            .map(rule => (rule, State[Id, TestFact, TestFact, Rule1[Id, TestFact]].enrich(rule, Set.empty, testFact1)))
            .map { case (rule, session) => State[Id, TestFact, TestFact, Rule1[Id, TestFact]].has(rule, session, testFact1) }
            .sample
            .get
            .shouldBe(true)
      )
  }

  test("State1 enrich hasn't applied fact") {
    arbitrary[TestFact1]
      .map(_.copy(x = 0))
      .flatMap(
        testFact1 =>
          arbitrary[Rule1[Id, TestFact]]
            .map(rule => (rule, State[Id, TestFact, TestFact, Rule1[Id, TestFact]].enrich(rule, Set.empty, testFact1)))
            .map { case (rule, session) => State[Id, TestFact, TestFact, Rule1[Id, TestFact]].has(rule, session, TestFact1(0)) }
            .sample
            .get
            .shouldBe(false)
      )
  }