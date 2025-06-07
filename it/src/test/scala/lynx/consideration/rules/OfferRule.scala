package lynx.consideration.rules

import cats.Applicative
import cats.implicits.*
import lynx.all.*
import lynx.consideration.domain.{Consideration, ExtraSum, NoExtra, Offer, Sum}
import lynx.rules.*

object OfferRule:
  def offerRule[F[_] : Applicative]: Rule2[F, Consideration] =
    rule2(
      "Offer rule",
      {
        case (Sum(sum), ExtraSum(extra)) => Offer(sum + extra).pure[F].map(++)
        case (Sum(sum), NoExtra)         => Offer(sum).pure[F].map(++)
      }
    )