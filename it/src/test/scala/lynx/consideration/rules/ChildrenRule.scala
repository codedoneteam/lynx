package lynx.consideration.rules

import cats.{Applicative, Functor}
import cats.implicits.*
import lynx.all.*
import lynx.consideration.domain.{Children, Consideration, ExtraSum, NoExtra}
import lynx.rules.Rule1

object ChildrenRule:
  def childrenRule[F[_] : Applicative : Functor]: Rule1[F, Consideration] =
    rule(
      "Children rule",
      {
        case Children(children) => (children >= 3).pure[F].ifF(ExtraSum(1000), NoExtra).map(++)
      }
    )