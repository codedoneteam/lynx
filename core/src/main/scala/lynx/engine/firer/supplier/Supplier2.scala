package lynx.engine.firer.supplier

import lynx._

object Supplier2 {
  implicit def makeSupplier2[F[_], A]: Supplier[F, A, (A, A)] =
    (facts: Facts[A]) =>
      for {
        facts1 <- facts
        facts2 <- facts
      } yield (facts1, facts2)
}
