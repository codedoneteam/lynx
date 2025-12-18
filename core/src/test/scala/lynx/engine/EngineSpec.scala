package lynx.engine

import cats.effect.{IO, Ref}
import lynx.engine.firer.Firer
import lynx.tagged._
import lynx.test.{TestFact, TestFact1}
import munit.CatsEffectSuite
import EngineSpec._
import lynx._
import lynx.test.tagged.TaggedTest.tagged1

class EngineSpec extends CatsEffectSuite {
  test("Run engine") {
    val io = for {
      sessionRef <- Ref.of[IO, Set[Applied[IO, TestFact]]](Set.empty)
      facts = Set[TestFact](TestFact1(0))
      result <- Engine[IO, TestFact].exec(sessionRef, IO.pure)(Set(tagged1[IO]), facts)
    } yield result

    io.assertEquals(Set[TestFact](TestFact1(0)))
  }
}

object EngineSpec {
  implicit def makeFirer1Mock[F[_]]: Firer[F, TestFact, TestFact, Tagged1[F, TestFact]] =
    new Firer[F, TestFact, TestFact, Tagged1[F, TestFact]] {
      override def fire(ref: Session[F, TestFact], refresh: Facts[TestFact] => F[Facts[TestFact]])(
        tagged: Tagged1[F, TestFact],
        facts: Facts[TestFact]
      ): F[Facts[TestFact]] = refresh(facts)
    }

  implicit def makeFirer2Mock[F[_]]: Firer[F, TestFact, (TestFact, TestFact), Tagged2[F, TestFact]] =
    new Firer[F, TestFact, (TestFact, TestFact), Tagged2[F, TestFact]] {
      override def fire(ref: Session[F, TestFact], refresh: Facts[TestFact] => F[Facts[TestFact]])(
        tagged: Tagged2[F, TestFact],
        facts: Facts[TestFact]
      ): F[Facts[TestFact]] = refresh(facts ++ Set[TestFact](TestFact1(1)))
    }

  implicit def makeFirerNMock[F[_]]: Firer[F, TestFact, Set[TestFact], TaggedN[F, TestFact]] =
    new Firer[F, TestFact, Set[TestFact], TaggedN[F, TestFact]] {
      override def fire(ref: Session[F, TestFact], refresh: Facts[TestFact] => F[Facts[TestFact]])(
        tagged: TaggedN[F, TestFact],
        facts: Facts[TestFact]
      ): F[Facts[TestFact]] = refresh(facts ++ Set[TestFact](TestFact1(1)))
    }
}
