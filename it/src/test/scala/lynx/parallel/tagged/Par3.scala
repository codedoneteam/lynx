package lynx.parallel.tagged

import cats.Monad
import cats.effect.Deferred
import cats.implicits._
import lynx.parallel.domain._
import lynx._

object Par3 {
  def par3[F[_] : Monad](deferred3: Deferred[F, Boolean]): PartialFunctionF[F, ParFact, ParFact] = {
    case ParFact2(x) =>
      deferred3
        .complete(true)
        .map(_ => ParFact3(x + 1))
  }
}
