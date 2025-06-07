package lynx.syntax

import cats.Functor
import cats.implicits.*

trait PartialFunctionSyntax:
  extension [F[_] : Functor, A, B, C](partialFunction: PartialFunction[A, F[B]])
    def map(f: B => C): PartialFunction[A, F[C]] = {
      case a if partialFunction.isDefinedAt(a) => partialFunction(a).map(f)
    }

object PartialFunctionSyntax extends PartialFunctionSyntax
