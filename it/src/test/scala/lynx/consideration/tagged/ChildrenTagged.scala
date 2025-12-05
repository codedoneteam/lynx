package lynx.consideration.tagged

import lynx.consideration.domain._

object ChildrenTagged {
  def children: PartialFunction[Consideration, Consideration] = {
    case Children(children) => if (children >= 3) ExtraSum(1000) else NoExtra
  }
}
