package lynx

import lynx.tagged._

object types {
  type Fact[A] = A

  type Facts[A] = Set[Fact[A]]

  type Tags[F[_], A] = Set[Tagged[F, A]]

  type PartialFunction1[F[_], A] = PartialFunction[A, F[Facts[A]]]

  type PartialFunction2[F[_], A] = PartialFunction[(A, A), F[Facts[A]]]

  type PartialFunctionN[F[_], A] = PartialFunction[Set[A], F[Facts[A]]]
}
