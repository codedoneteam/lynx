package lynx.consideration.rules

import cats.{Applicative, Functor}
import cats.implicits.*
import lynx.all.*
import lynx.consideration.domain.*
import lynx.rules.*

object AcceptRule:
  def acceptRule[F[_] : Applicative : Functor]: Rule1[F, Consideration] =
    rule(
      "Accept rule",
      {
        case Offer(sum) => Accept(sum).pure[F].map(++)
      }
    )
