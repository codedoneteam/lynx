package lynx.test.gens

import lynx.test.TestFact.*
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

object TestFactsGens:
  given Arbitrary[TestFact1] = Arbitrary(testFact1Gen)

  def testFact1Gen: Gen[TestFact1] = arbitrary[Int].map(x => TestFact1(x))
