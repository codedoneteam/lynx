package lynx

import lynx.rules.*

object types:
  type Fact[A] = A

  type Facts[A] = Set[Fact[A]]

  type RuleName = String

  type Rules[F[_], A] = Set[Rule[F, A]]

  type Session[F[_], A] = Set[Applied[F, A]]
