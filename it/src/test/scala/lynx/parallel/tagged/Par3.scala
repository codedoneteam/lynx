package lynx.parallel.tagged

import cats.Monad
import cats.effect.Deferred
import cats.implicits._
import lynx.all._
import lynx.parallel.domain._
import lynx.types.PartialFunction1

object Par3 {
  def par3[F[_] : Monad](deferred3: Deferred[F, Boolean]): PartialFunction1[F, ParFact] = {
    case ParFact2(x) =>
      deferred3
        .complete(true)
        .map(_ => Set(ParFact3(x + 1)))
  }
}
