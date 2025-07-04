package lynx.runner

import cats.Applicative
import cats.effect.{IO, Ref}
import cats.implicits._
import lynx.engine.firer.Firer
import lynx.tagged._
import lynx.test.{TestFact, TestFact1}
import lynx.types.Facts
import munit.CatsEffectSuite
import RunnerSpec._
import lynx.folder.Folder
import lynx.session.Session
import lynx.test.tagged.TaggedTest.tagged1

class RunnerSpec extends CatsEffectSuite {
  test("Runner") {
    Runner[IO, TestFact, Option[Int], Int]
      .run(0, 1)(Set(tagged1[IO]), Set(TestFact1(0)))
      .value
      .assertEquals(0.some.asRight)
  }
}

object RunnerSpec {
  implicit def makeFolder: Folder[TestFact, Option[Int]] = (facts: Facts[TestFact]) => facts.collectFirst { case TestFact1(0) => 0 }

  implicit def makeFirer1Mock[F[_] : Applicative]: Firer[F, TestFact, TestFact, Tagged1[F, TestFact], Int] =
    (_: Ref[F, Session[F, TestFact]], _: Int, _: Tagged1[F, TestFact], facts: Facts[TestFact], _: Int, _: Int) => facts.pure[F]

  implicit def makeFirer2Mock[F[_] : Applicative]: Firer[F, TestFact, (TestFact, TestFact), Tagged2[F, TestFact], Int] =
    (_: Ref[F, Session[F, TestFact]], _: Int, _: Tagged2[F, TestFact], _: Facts[TestFact], _: Int, _: Int) =>
      Set[TestFact](TestFact1(1)).pure[F]

  implicit def makeFirerNMock[F[_] : Applicative]: Firer[F, TestFact, Set[TestFact], TaggedN[F, TestFact], Int] =
    (_: Ref[F, Session[F, TestFact]], _: Int, _: TaggedN[F, TestFact], _: Facts[TestFact], _: Int, _: Int) =>
      Set[TestFact](TestFact1(1)).pure[F]
}
