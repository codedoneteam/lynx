package lynx.engine.firer.lock

import cats.effect.{Ref, SyncIO}
import cats.implicits._
import cats.Applicative
import lynx.engine.firer.lock.Lock2.makeLock2
import lynx.engine.firer.lock.Lock2Spec._
import lynx.tagged._
import munit.CatsEffectSuite

class Lock2Spec extends CatsEffectSuite {
  test("Lock2") {
    test("Lock2 has not state") {
      Ref
        .of[SyncIO, Set[Applied[SyncIO, Int]]](Set.empty)
        .flatMap(session => Lock[SyncIO, Int, (Int, Int), Tagged2[SyncIO, Int]].lock(Tagged2(pf[SyncIO]), session, (0, 0)))
        .assertEquals(false)
    }

    test("Lock2 updated") {
      Ref
        .of[SyncIO, Set[Applied[SyncIO, Int]]](Set.empty)
        .flatMap(session => Lock[SyncIO, Int, (Int, Int), Tagged2[SyncIO, Int]].lock(Tagged2(pf[SyncIO]), session, (0, 0)).as(session))
        .flatMap(_.get)
        .map(_.size)
        .assertEquals(1)
    }

    test("Lock2 has not state") {
      SyncIO
        .pure(Tagged2(pf[SyncIO]))
        .flatMap(
          tagged =>
            Ref
              .of[SyncIO, Set[Applied[SyncIO, Int]]](Set(Applied2(tagged, 0 -> 0)))
              .flatMap(session => Lock[SyncIO, Int, (Int, Int), Tagged2[SyncIO, Int]].lock(tagged, session, (0, 0)))
        )
        .assertEquals(true)
    }
  }
}

object Lock2Spec {
  def pf[F[_] : Applicative]: PartialFunction[(Int, Int), F[Set[Int] => Set[Int]]] = {
    case _ => ((facts: Set[Int]) => facts + 1).pure[F]
  }
}
