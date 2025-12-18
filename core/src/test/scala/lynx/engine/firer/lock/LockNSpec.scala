package lynx.engine.firer.lock

import cats.effect.{Ref, SyncIO}
import cats.implicits._
import cats.Applicative
import lynx.engine.firer.lock.LockN.makeLockN
import lynx.engine.firer.lock.LockNSpec._
import lynx.tagged._
import munit.CatsEffectSuite

class LockNSpec extends CatsEffectSuite {
  test("LockN has not state") {
    Ref
      .of[SyncIO, Set[Applied[SyncIO, Int]]](Set.empty)
      .flatMap(session => Lock[SyncIO, Int, Set[Int], TaggedN[SyncIO, Int]].lock(TaggedN(pf[SyncIO]), session, Set(0)))
      .assertEquals(false)
  }

  test("LockN updated") {
    Ref
      .of[SyncIO, Set[Applied[SyncIO, Int]]](Set.empty)
      .flatMap(session => Lock[SyncIO, Int, Set[Int], TaggedN[SyncIO, Int]].lock(TaggedN(pf[SyncIO]), session, Set(0)).as(session))
      .flatMap(_.get)
      .map(_.size)
      .assertEquals(1)
  }

  test("LockN has not state") {
    SyncIO
      .pure(TaggedN(pf[SyncIO]))
      .flatMap(
        tagged =>
          Ref
            .of[SyncIO, Set[Applied[SyncIO, Int]]](Set(AppliedN(tagged, Set(0))))
            .flatMap(session => Lock[SyncIO, Int, Set[Int], TaggedN[SyncIO, Int]].lock(tagged, session, Set(0)))
      )
      .assertEquals(true)
  }
}

object LockNSpec {
  def pf[F[_] : Applicative]: PartialFunction[Set[Int], F[Set[Int] => Set[Int]]] = {
    case _ => ((facts: Set[Int]) => facts + 1).pure[F]
  }
}
