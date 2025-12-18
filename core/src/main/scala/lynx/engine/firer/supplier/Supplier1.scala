package lynx.engine.firer.supplier

import lynx._

object Supplier1 {
  implicit def makeSupplier1[F[_], A]: Supplier[F, A, A] = (facts: Facts[A]) => facts
}
