package lynx.engine.firer.lock

import cats.implicits._
import cats.Applicative
import lynx.tagged.{Applied, Applied1, Tagged1}
import Lock1Spec._
import cats.effect.{Ref, SyncIO}
import lynx.engine.firer.lock.Lock1.makeLock1
import munit.CatsEffectSuite

class Lock1Spec extends CatsEffectSuite {
  test("Lock1 has not state") {
    Ref
      .of[SyncIO, Set[Applied[SyncIO, Int]]](Set.empty)
      .flatMap(session => Lock[SyncIO, Int, Int, Tagged1[SyncIO, Int]].lock(Tagged1(pf[SyncIO]), session, 0))
      .assertEquals(false)
  }

  test("Lock1 updated") {
    Ref
      .of[SyncIO, Set[Applied[SyncIO, Int]]](Set.empty)
      .flatMap(session => Lock[SyncIO, Int, Int, Tagged1[SyncIO, Int]].lock(Tagged1(pf[SyncIO]), session, 0).as(session))
      .flatMap(_.get)
      .map(_.size)
      .assertEquals(1)
  }

  test("Lock1 has not state") {
    SyncIO
      .pure(Tagged1(pf[SyncIO]))
      .flatMap(
        tagged =>
          Ref
            .of[SyncIO, Set[Applied[SyncIO, Int]]](Set(Applied1(tagged, 0)))
            .flatMap(session => Lock[SyncIO, Int, Int, Tagged1[SyncIO, Int]].lock(tagged, session, 0))
      )
      .assertEquals(true)
  }
}

object Lock1Spec {
  def pf[F[_] : Applicative]: PartialFunction[Int, F[Set[Int] => Set[Int]]] = {
    case _ => ((facts: Set[Int]) => facts + 1).pure[F]
  }
}
