package lynx.consideration.tagged

import lynx.consideration.domain._

object OfferTagged {
  def offer: PartialFunction[(Consideration, Consideration), Consideration] = {
    case (Sum(sum), ExtraSum(extra)) => Offer(sum + extra)
    case (Sum(sum), NoExtra)         => Offer(sum)
  }
}
