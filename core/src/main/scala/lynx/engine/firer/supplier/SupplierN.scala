package lynx.engine.firer.supplier

import lynx.types.Facts

object SupplierN:
  given [F[_], A]: Supplier[F, A, Set[A]] = (facts: Facts[A]) => Set(facts)
