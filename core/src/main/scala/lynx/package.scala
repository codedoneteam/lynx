import cats.effect.Ref
import lynx.tagged._

package object lynx {
  type Fact[A] = A

  type Facts[A] = Set[Fact[A]]

  type Tags[F[_], A] = Set[Tagged[F, A]]

  type Counter[F[_]] = Ref[F, Int]

  type Session[F[_], A] = Ref[F, Set[Applied[F, A]]]

  type PartialFunctionF[F[_], A, B] = PartialFunction[A, F[B]]

  type PartialFunction2F[F[_], A, B] = PartialFunction[(A, A), F[B]]

  type PartialFunctionNF[F[_], A, B] = PartialFunction[Set[A], F[B]]
}
