package lynx.state.tagged

import cats.effect.kernel.{Clock, Temporal}
import cats.implicits._
import lynx.state.convert.Convert.toLocalDateTime
import lynx.state.domain._
import lynx.types.PartialFunction1

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

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
