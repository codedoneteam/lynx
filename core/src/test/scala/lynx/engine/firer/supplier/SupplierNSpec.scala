package lynx.engine.firer.supplier

import cats.Id
import lynx.engine.firer.supplier.SupplierN.makeSupplierN
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SupplierNSpec extends AnyFunSuite with Matchers {
  Supplier[Id, Int, Set[Int]].supply(Set(0)).shouldBe(Set(Set(0)))
}
