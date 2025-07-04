package lynx.engine.firer

import cats.effect.{IO, Ref}
import cats.kernel.Monoid
import lynx.engine.firer.FirerSpec._
import lynx.engine.firer.apply.Apply
import lynx.engine.firer.state.State
import lynx.engine.firer.supplier.Supplier
import lynx.tagged.Tagged1
import lynx.session.Session
import lynx.session.SessionMonoid.sessionMonoid
import lynx.test.tagged.TaggedTest.tagged1
import lynx.test.{TestFact, TestFact1}
import lynx.types._
import munit.CatsEffectSuite
import org.scalacheck.Arbitrary.arbitrary

class FirerSpec extends CatsEffectSuite {
  test("Fire tagged") {
    Ref
      .of[IO, Session[IO, TestFact]](Monoid[Session[IO, TestFact]].empty)
      .flatMap(
        session =>
          Firer[IO, TestFact, TestFact, Tagged1[IO, TestFact], Int]
            .fire(session, 0, tagged1[IO], Set(TestFact1(0)), 0, 1)
      )
      .assertEquals(Set[TestFact](TestFact1(0)))
  }
}

object FirerSpec {
  implicit def makeState1Mock[F[_]]: State[F, TestFact, TestFact, Tagged1[F, TestFact]] =
    new State[F, TestFact, TestFact, Tagged1[F, TestFact]] {
      override def enrich(tagged: Tagged1[F, TestFact], session: Session[F, TestFact], input: TestFact): Session[F, TestFact] = session

      override def has(tagged: Tagged1[F, TestFact], session: Session[F, TestFact], input: TestFact): Boolean = input == TestFact1(0)
    }

  implicit def makeApplyMock[F[_]]: Apply[F, TestFact, TestFact, Tagged1[F, TestFact]] = (tagged: Tagged1[F, TestFact]) => tagged.pf

  implicit def makeSupplierMock[F[_]]: Supplier[F, TestFact, TestFact] = (facts: Facts[TestFact]) => facts
}
