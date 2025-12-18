package lynx.parallel.tagged

import cats.implicits._
import cats.Monad
import cats.effect.Deferred
import lynx.parallel.domain._
import lynx._

object Par1 {
  def par1[F[_] : Monad](
    deferred1: Deferred[F, Boolean],
    deferred2: Deferred[F, Boolean],
    deferred3: Deferred[F, Boolean]
  ): PartialFunctionF[F, ParFact, ParFact] = {
    case Start(x) =>
      deferred1
        .complete(true)
        .flatMap(_ => deferred2.get)
        .flatMap(_ => deferred3.get)
        .map(_ => ParFact1(x + 1))
  }
}
