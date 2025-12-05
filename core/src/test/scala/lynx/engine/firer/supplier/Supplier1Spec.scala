package lynx.engine.firer.supplier

import cats.Id
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import lynx.engine.firer.supplier.Supplier1.makeSupplier1

class Supplier1Spec extends AnyFunSuite with Matchers {
  Supplier[Id, Int, Int].supply(Set(0, 1)).shouldBe(Set(0, 1))
}
