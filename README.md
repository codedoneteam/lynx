## Lynx library

Business rules engine. Provides firing stateful rules. Rules can be combined in an independent order.

Example
````
import cats.implicits._
import cats.effect.kernel.{Clock, Temporal}
import lynx.state.convert.Convert.toLocalDateTime
import lynx.types.PartialFunction1
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import java.time.LocalDateTime

sealed trait State
case object Start extends State
final case class Wait(now: LocalDateTime, till: LocalDateTime) extends State
case object End extends State

object Await {
  def await[F[_] : Temporal : Clock]: PartialFunction1[F, State] = {
    case Wait(_, till) =>
      for {
        now <- Clock[F].realTime.map(now => toLocalDateTime(now.toMillis))
        state <-
          now
            .isBefore(till)
            .pure[F]
            .ifM(Temporal[F].sleep(FiniteDuration(100, TimeUnit.MILLISECONDS)).as[State](Wait(now, till)), End.pure[F].widen[State])
      } yield Set(state)
  }
}

object StartTagged {
  def start[F[_] : Applicative : Functor : Clock]: PartialFunction1[F, State] = {
    case Start =>
      for {
        now <- Clock[F].realTime.map(now => toLocalDateTime(now.toMillis))
        till = now.plusSeconds(1)
        wait = Wait(now, till)
      } yield Set(wait)
  }
}

implicit val makeFolder: Folder[State, Option[End.type]] = (facts: Facts[State]) => facts.collectFirst { case End => End }

def tags[F[_] : Async : Clock]: Tags[F, State] = start[F] <+> await[F]

 tags[IO].fire(0)(Start)
````
