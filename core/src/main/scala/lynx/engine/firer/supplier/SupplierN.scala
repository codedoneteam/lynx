package lynx.engine.firer.supplier

import lynx._

object SupplierN {
  implicit def makeSupplierN[F[_], A]: Supplier[F, A, Set[A]] = (facts: Facts[A]) => Set(facts)
}
