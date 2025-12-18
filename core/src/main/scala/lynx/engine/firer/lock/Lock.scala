package lynx.engine.firer.lock

import lynx.tagged._
import lynx._

trait Lock[F[_], A, I, R <: Tagged[F, A]] {
  def lock(tagged: R, session: Session[F, A], input: I): F[Boolean]
}

object Lock {
  def apply[F[_], A, I, R <: Tagged[F, A]](implicit applied: Lock[F, A, I, R]): Lock[F, A, I, R] = applied
}
