## Lynx library

Apply partial functions on set of facts.

1) Partial functions can be combined in an independent order;
2) Partial functions applying is parallel and asynchronous;
3) Partial function may be effectful;
4) Partial function may be recursive.

Example
````
import cats.implicits._
import cats.effect.kernel.{Clock, Temporal}
import lynx.state.convert.Convert.toLocalDateTime
import lynx.types.PartialFunctionF
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration
import java.time.LocalDateTime

sealed trait State
case object Start extends State
final case class Wait(now: LocalDateTime, till: LocalDateTime) extends State
case object End extends State
case object Finish extends State

def await[F[_] : Temporal : Clock]: PartialFunctionF[F, State, State] = {
    case Wait(_, till) =>
      for {
        now <- Clock[F].realTime.map(now => toLocalDateTime(now.toMillis))
        state <-
          now
            .isBefore(till)
            .pure[F]
            .ifM(Temporal[F].sleep(FiniteDuration(100, TimeUnit.MILLISECONDS)).as[State](Wait(now, till)), End.pure[F].widen[State])
      } yield state
  }

def start[F[_] : Applicative : Functor : Clock]: PartialFunctionF[F, State, State] = {
    case Start =>
      for {
        now <- Clock[F].realTime.map(now => toLocalDateTime(now.toMillis))
        till = now.plusSeconds(1)
        wait = Wait(now, till)
      } yield wait
  }

def finish: PartialFunction[State, State] = {
   case End => Finish
}

implicit val makeFolder: Folder[State, Option[End.type]] = (facts: Facts[State]) => facts.collectFirst { case End => End }

def workflow[F[_] : Async : Clock]: Tags[F, State] = start[F] <+> finish <+> await[F]

workflow[IO].fire(0)(Start)
````