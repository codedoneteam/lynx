package lynx.engine

import cats.implicits._
import cats.effect.{IO, Ref}
import lynx.engine.firer.Firer
import lynx.tagged._
import lynx.test.{TestFact, TestFact1}
import munit.CatsEffectSuite
import EngineSpec._
import cats.{Applicative, Monoid}
import lynx.session.Session
import lynx.types._
import lynx.session.SessionMonoid.sessionMonoid
import lynx.test.tagged.TaggedTest.tagged1

class EngineSpec extends CatsEffectSuite {
  test("Run engine") {
    val io = for {
      session <- Ref.of[IO, Session[IO, TestFact]](Monoid[Session[IO, TestFact]].empty)
      facts   <- Engine[IO, TestFact, Int].exec(session)(0, Set(tagged1[IO]), Set(TestFact1(0)), 0, 1)
    } yield facts

    io.assertEquals(Set[TestFact](TestFact1(0)))
  }
}

object EngineSpec {
  implicit def makeFirer1Mock[F[_] : Applicative]: Firer[F, TestFact, TestFact, Tagged1[F, TestFact], Int] =
    (_: Ref[F, Session[F, TestFact]], _: Int, _: Tagged1[F, TestFact], facts: Facts[TestFact], _: Int, _: Int) => facts.pure[F]

  implicit def makeFirer2Mock[F[_] : Applicative]: Firer[F, TestFact, (TestFact, TestFact), Tagged2[F, TestFact], Int] =
    (_: Ref[F, Session[F, TestFact]], _: Int, _: Tagged2[F, TestFact], _: Facts[TestFact], _: Int, _: Int) =>
      Set[TestFact](TestFact1(1)).pure[F]

  implicit def makeFirerNMock[F[_] : Applicative]: Firer[F, TestFact, Set[TestFact], TaggedN[F, TestFact], Int] =
    (_: Ref[F, Session[F, TestFact]], _: Int, _: TaggedN[F, TestFact], _: Facts[TestFact], _: Int, _: Int) =>
      Set[TestFact](TestFact1(1)).pure[F]
}
