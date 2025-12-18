package lynx.runner

import lynx._
import cats.effect.IO
import cats.implicits._
import lynx.engine.firer.Firer
import lynx.tagged._
import lynx.test.{TestFact, TestFact1}
import munit.CatsEffectSuite
import RunnerSpec._
import cats.Applicative
import lynx.folder.Folder
import lynx.test.tagged.TaggedTest.tagged1

class RunnerSpec extends CatsEffectSuite {
  test("Runner") {
    Runner[IO, TestFact, Option[Int]]
      .run(10)(Set(tagged1[IO]), Set(TestFact1(0)))
      .value
      .assertEquals(0.some.asRight)
  }
}

object RunnerSpec {
  implicit def makeFolder: Folder[TestFact, Option[Int]] = (facts: Facts[TestFact]) => facts.collectFirst { case TestFact1(0) => 0 }

  implicit def makeFirer1Mock[F[_] : Applicative]: Firer[F, TestFact, TestFact, Tagged1[F, TestFact]] =
    new Firer[F, TestFact, TestFact, Tagged1[F, TestFact]] {
      override def fire(session: Session[F, TestFact], refresh: Facts[TestFact] => F[Facts[TestFact]])(
        tagged: Tagged1[F, TestFact],
        facts: Facts[TestFact]
      ): F[Facts[TestFact]] = facts.pure[F]
    }

  implicit def makeFirer2Mock[F[_]]: Firer[F, TestFact, (TestFact, TestFact), Tagged2[F, TestFact]] =
    new Firer[F, TestFact, (TestFact, TestFact), Tagged2[F, TestFact]] {
      override def fire(session: Session[F, TestFact], refresh: Facts[TestFact] => F[Facts[TestFact]])(
        tagged: Tagged2[F, TestFact],
        facts: Facts[TestFact]
      ): F[Facts[TestFact]] = refresh(facts + TestFact1(1))
    }

  implicit def makeFirerNMock[F[_]]: Firer[F, TestFact, Set[TestFact], TaggedN[F, TestFact]] =
    new Firer[F, TestFact, Set[TestFact], TaggedN[F, TestFact]] {
      override def fire(session: Session[F, TestFact], refresh: Facts[TestFact] => F[Facts[TestFact]])(
        tagged: TaggedN[F, TestFact],
        facts: Facts[TestFact]
      ): F[Facts[TestFact]] = refresh(facts + TestFact1(1))
    }
}
