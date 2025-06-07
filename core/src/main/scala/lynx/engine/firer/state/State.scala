package lynx.engine.firer.state

import lynx.rules.*
import lynx.types.Session

trait State[F[_], A, I, R <: Rule[F, A]]:
  def enrich(rule: R, session: Session[F, A], input: I): Session[F, A]

  def has(rule: R, session: Session[F, A], input: I): Boolean

object State:
  def apply[F[_], A, I, R <: Rule[F, A]](using applied: State[F, A, I, R]): State[F, A, I, R] = applied
