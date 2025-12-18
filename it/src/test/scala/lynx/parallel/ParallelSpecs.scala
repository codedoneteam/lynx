package lynx.parallel

import lynx._
import cats.implicits._
import lynx.all._
import munit.CatsEffectSuite
import ParallelSpecs._
import cats.effect.{Deferred, IO, Sync}
import lynx.folder.Folder
import lynx.parallel.domain._
import lynx.parallel.tagged.Par1._
import lynx.parallel.tagged.Par2._
import lynx.parallel.tagged.Par3._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class ParallelSpecs extends CatsEffectSuite {
  test("Par") {
    val io = for {
      deferred1 <- Deferred[IO, Boolean]
      deferred2 <- Deferred[IO, Boolean]
      deferred3 <- Deferred[IO, Boolean]
      fired     <- tags[IO](deferred1, deferred2, deferred3).fire()(Start(0)).value
    } yield fired

    io.assertEquals(Set[ParFact](Start(0), ParFact1(1), ParFact2(1), ParFact3(2)).asRight)
  }
}

object ParallelSpecs {
  implicit val makeFolder: Folder[ParFact, Set[ParFact]] = (facts: Facts[ParFact]) => facts

  def tags[F[_] : Sync](
    deferred1: Deferred[F, Boolean],
    deferred2: Deferred[F, Boolean],
    deferred3: Deferred[F, Boolean]
  ): Tags[F, ParFact] = par1(deferred1, deferred2, deferred3) <+> par2(deferred1, deferred2) <+> par3(deferred3)
}
