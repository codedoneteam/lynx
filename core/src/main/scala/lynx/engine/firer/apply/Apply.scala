package lynx.engine.firer.apply

import lynx.tagged.Tagged
import lynx._

trait Apply[F[_], A, I, R <: Tagged[F, A]] {
  def applyFunction(tagged: R): PartialFunction[I, F[Facts[A] => Facts[A]]]
}

object Apply {
  def apply[F[_], A, I, R <: Tagged[F, A]](implicit apply: Apply[F, A, I, R]): Apply[F, A, I, R] = apply
}
