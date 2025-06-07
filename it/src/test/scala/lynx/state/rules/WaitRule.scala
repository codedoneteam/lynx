package lynx.state.rules

import cats.effect.kernel.{Clock, Temporal}
import cats.implicits.*
import lynx.all.*
import lynx.rules.Rule1
import lynx.state.convert.Convert.toLocalDateTime
import lynx.state.domain.*
import lynx.state.domain.State.{End, Wait}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

object WaitRule:
  def waitRule[F[_] : Temporal : Clock]: Rule1[F, State] =
    rule(
      "Wait rule",
      {
        case Wait(till) =>
          for {
            now <- Clock[F].realTime.map(now => toLocalDateTime(now.toMillis))
            state <-
              now
                .isBefore(till)
                .pure[F]
                .ifM(Temporal[F].sleep(FiniteDuration(100, TimeUnit.MILLISECONDS)).as[State](Wait(till)), End.pure[F].widen[State])
          } yield >>(state)
      }
    )