package lynx.state.tagged

import cats.implicits._
import cats.{Applicative, Functor}
import cats.effect.Clock
import lynx.state.convert.Convert.toLocalDateTime
import lynx.state.domain._
import lynx._

object StartTagged {
  def start[F[_] : Applicative : Functor : Clock]: PartialFunctionF[F, State, State] = {
    case Start =>
      for {
        now <- Clock[F].realTime.map(now => toLocalDateTime(now.toMillis))
        till = now.plusSeconds(1)
        wait = Wait(now, till)
      } yield wait
  }
}
