package lynx.parallel.tagged

import cats.Monad
import cats.effect.Deferred
import cats.implicits._
import lynx.parallel.domain._
import lynx._

object Par2 {
  def par2[F[_] : Monad](deferred1: Deferred[F, Boolean], deferred2: Deferred[F, Boolean]): PartialFunctionF[F, ParFact, ParFact] = {
    case Start(x) =>
      deferred2
        .complete(true)
        .flatMap(_ => deferred1.get)
        .map(_ => ParFact2(x + 1))
  }
}
