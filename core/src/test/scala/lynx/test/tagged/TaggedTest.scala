package lynx.test.tagged

import cats.effect.kernel.Sync
import cats.{Applicative, Functor}
import lynx.tagged.{Mapper, Tagged1}
import lynx.test.{TestFact, TestFact1}

object TaggedTest {
  def tagged1[F[_] : Sync]: Tagged1[F, TestFact] = Tagged1[F, TestFact](Mapper.map(pf))

  def pf[F[_] : Applicative : Functor]: PartialFunction[TestFact, TestFact] = {
    case TestFact1(0) => TestFact1(1)
  }
}
