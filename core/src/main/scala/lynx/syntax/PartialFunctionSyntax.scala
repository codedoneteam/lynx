package lynx.syntax

import cats.Functor
import cats.effect.kernel.Sync
import cats.implicits._

trait PartialFunctionSyntax {
  implicit class PartialFunctionSyntaxOps[F[_] : Sync, A, B, C](partialFunction: PartialFunction[A, B]) {
    def map(f: B => C): PartialFunction[A, F[C]] = {
      case x if partialFunction.isDefinedAt(x) => Sync[F].delay(partialFunction(x)).map(f)
    }
  }

  implicit class PartialFunctionFSyntaxOps[F[_] : Functor, A, B, C](partialFunction: PartialFunction[A, F[B]]) {
    def map(f: B => C): PartialFunction[A, F[C]] = {
      case x if partialFunction.isDefinedAt(x) => partialFunction(x).map(f)
    }
  }
}

object PartialFunctionSyntax extends PartialFunctionSyntax
