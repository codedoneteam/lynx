package lynx.engine.firer.supplier

import lynx._

trait Supplier[F[_], A, I] {
  def supply(facts: Facts[A]): Set[I]
}

object Supplier {
  def apply[F[_], A, I](implicit supplier: Supplier[F, A, I]): Supplier[F, A, I] = supplier
}
