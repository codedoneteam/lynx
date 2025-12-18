package lynx.engine.firer

import cats.implicits._
import cats.effect.kernel.Sync
import cats.effect.{IO, Ref}
import lynx.engine.firer.FirerSpec._
import lynx.engine.firer.apply.Apply
import lynx.engine.firer.lock.Lock
import lynx.engine.firer.supplier.Supplier
import lynx.tagged.{Applied, Applied1, Tagged1}
import lynx.test.tagged.TaggedTest.tagged1
import lynx.test.{TestFact, TestFact1}
import munit.CatsEffectSuite
import lynx._

class FirerSpec extends CatsEffectSuite {
  test("Fire tagged") {
    Ref
      .of[IO, Set[Applied[IO, TestFact]]](Set.empty)
      .flatMap(
        session =>
          Firer[IO, TestFact, TestFact, Tagged1[IO, TestFact]]
            .fire(session, IO.pure)(tagged1[IO], Set(TestFact1(0)))
      )
      .assertEquals(Set[TestFact](TestFact1(0), TestFact1(1)))
  }
}

object FirerSpec {
  implicit def makeLock1Mock[F[_] : Sync]: Lock[F, TestFact, TestFact, Tagged1[F, TestFact]] =
    (_: Tagged1[F, TestFact], session: Session[F, TestFact], input: TestFact) => session.get.map(_.contains(Applied1(tagged1, input)))

  implicit def makeApplyMock[F[_]]: Apply[F, TestFact, TestFact, Tagged1[F, TestFact]] = (tagged: Tagged1[F, TestFact]) => tagged.pf

  implicit def makeSupplierMock[F[_]]: Supplier[F, TestFact, TestFact] = (facts: Facts[TestFact]) => facts
}
