package lynx.tagged

import lynx.types._
import cats.Functor
import cats.effect.Sync
import lynx.syntax.PartialFunctionSyntax._

object Mapper {
  def map[F[_] : Sync, A, B <: A, I](pf: PartialFunction[I, B]): PartialFunction[I, F[Facts[A] => Facts[A]]] =
    pf.map(x => (facts: Facts[A]) => facts + x)

  def mapF[F[_] : Functor, A, B <: A, I](pf: PartialFunction[I, F[Facts[B]]]): PartialFunction[I, F[Facts[A] => Facts[A]]] =
    pf.map(add => (facts: Facts[A]) => facts ++ add)
}
