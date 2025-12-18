package lynx.test.gens

import lynx.test.TestFact1
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

object TestFactsGens {
  def testFact1Gen: Gen[TestFact1] = arbitrary[Int].map(TestFact1)

  object implicits {
    implicit def arbTestFact1: Arbitrary[TestFact1] = Arbitrary(testFact1Gen)
  }
}
