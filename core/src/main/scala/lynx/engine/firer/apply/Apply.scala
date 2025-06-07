package lynx.engine.firer.apply

import lynx.rules.Rule
import lynx.types.Facts

trait Apply[F[_], A, I, R <: Rule[F, A]]:
  def applyFunction(rule: R): PartialFunction[I, F[Facts[A] => Facts[A]]]

object Apply:
  def apply[F[_], A, I, R <: Rule[F, A]](using apply: Apply[F, A, I, R]): Apply[F, A, I, R] = apply
