package lynx.engine.firer.supplier

import cats.Id
import lynx.engine.firer.supplier.Supplier2.makeSupplier2
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class Supplier2Spec extends AnyFunSuite with Matchers {
  Supplier[Id, Int, (Int, Int)].supply(Set(0, 1)).shouldBe(Set((0, 0), (0, 1), (1, 0), (1, 1)))
}
