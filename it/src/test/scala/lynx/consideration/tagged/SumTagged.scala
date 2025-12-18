package lynx.consideration.tagged

import lynx.consideration.domain._

object SumTagged {
  def sum: PartialFunction[Set[Consideration], Consideration] = {
    case facts if facts.collect { case sum @ Sum(_) => sum }.isEmpty =>
      Sum(facts.collect { case Income(_, income) => income }.map(_ * 2).fold(BigDecimal(0))(_ + _))
  }
}
