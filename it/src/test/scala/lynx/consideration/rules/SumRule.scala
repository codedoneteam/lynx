package lynx.consideration.rules

import cats.Applicative
import cats.implicits.*
import lynx.all.*
import lynx.consideration.domain
import lynx.consideration.domain.{Consideration, Income, Sum}
import lynx.rules.*

object SumRule:
  def sumRule[F[_] : Applicative]: RuleN[F, Consideration] =
    ruleN(
      "Sum rule",
      {
        case facts if facts.collect { case sum @ Sum(_) => sum }.isEmpty =>
          Sum(facts.collect { case Income(_, income) => income }.map(_ * 2).fold(BigDecimal(0))(_ + _)).pure[F].map(++)
      }
    )