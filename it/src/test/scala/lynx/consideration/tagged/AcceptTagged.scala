package lynx.consideration.tagged

import lynx.consideration.domain._

object AcceptTagged {
  def accept: PartialFunction[Consideration, Consideration] = {
    case Offer(sum) => Accept(sum)
  }
}
