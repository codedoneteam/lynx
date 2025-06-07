package lynx.engine.firer.supplier

import lynx.types.Facts

trait Supplier[F[_], A, I]:
  def supply(facts: Facts[A]): Set[I]

object Supplier:
  def apply[F[_], A, I](using supplier: Supplier[F, A, I]): Supplier[F, A, I] = supplier
