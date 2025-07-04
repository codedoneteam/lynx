package lynx.consideration.tagged

import lynx.consideration.domain._

object DenyTagged {
  def deny: PartialFunction[Consideration, Consideration] = {
    case Age(age) if (age < 21) || (age > 70) => Deny
  }
}
