package lynx.state.rules

import cats.implicits.*
import cats.{Applicative, Functor}
import cats.effect.Clock
import lynx.all.*
import lynx.rules.Rule1
import lynx.state.convert.Convert.toLocalDateTime
import lynx.state.domain.*
import lynx.state.domain.State.{Start, Wait}

object StartRule:
  def startRule[F[_] : Applicative : Functor : Clock]: Rule1[F, State] =
    rule(
      "Start rule",
      {
        case Start =>
          for {
            now <- Clock[F].realTime.map(now => toLocalDateTime(now.toMillis))
            till = now.plusSeconds(1)
            wait = Wait(till)
          } yield >>(wait)
      }
    )