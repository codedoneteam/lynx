package lynx.engine.firer.state

import lynx.tagged._
import lynx.session.Session

trait State[F[_], A, I, R <: Tagged[F, A]] {
  def enrich(tagged: R, session: Session[F, A], input: I): Session[F, A]

  def has(tagged: R, session: Session[F, A], input: I): Boolean
}

object State {
  def apply[F[_], A, I, R <: Tagged[F, A]](implicit applied: State[F, A, I, R]): State[F, A, I, R] = applied
}
