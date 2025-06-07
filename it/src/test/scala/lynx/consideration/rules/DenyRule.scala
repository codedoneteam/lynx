package lynx.consideration.rules

import cats.implicits.*
import cats.Applicative
import lynx.all.*
import lynx.consideration.domain.{Age, Consideration, Deny}
import lynx.rules.Rule1

object DenyRule:
  def denyRule[F[_] : Applicative]: Rule1[F, Consideration] =
    rule(
      "Deny rule",
      {
        case Age(age) if (age < 21) || (age > 70) => Deny.pure[F].map(>>)
      }
    )