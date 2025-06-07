package lynx.engine.firer.supplier

import lynx.types.Facts

object Supplier1:
  given [F[_], A]: Supplier[F, A, A] = (facts: Facts[A]) => facts
