package lynx.state

import lynx._
import lynx.all._
import cats.implicits._
import StateItSpec._
import cats.effect.{IO, Sync}
import cats.effect.kernel.Async
import lynx.folder.Folder
import lynx.state.domain.State
import lynx.state.domain.Start
import lynx.state.domain.End
import lynx.state.tagged.StartTagged.start
import lynx.state.tagged.Await._
import munit.CatsEffectSuite
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class StateItSpec extends CatsEffectSuite {
  test("Lock") {
    tags[IO]
      .fire()(Start)
      .value
      .assertEquals(End.some.asRight)
  }
}

object StateItSpec {
  implicit val makeFolder: Folder[State, Option[End.type]] = (facts: Facts[State]) => facts.collectFirst { case End => End }

  implicit def makeLogger[F[_] : Sync]: Logger[F] = Slf4jLogger.getLoggerFromName("Logger")

  def tags[F[_] : Async]: Tags[F, State] = start[F] <+> await[F]
}
