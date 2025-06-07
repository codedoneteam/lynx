package lynx.rules

import lynx.types.*
import cats.Functor
import lynx.engine.*
import lynx.syntax.PartialFunctionSyntax.*
import lynx.rules.Action.*

sealed trait Rule[F[_], A]:
  def name: RuleName

final case class Rule1[F[_], A](name: RuleName, apply: PartialFunction[A, F[Facts[A] => Facts[A]]]) extends Rule[F, A]

final case class Rule2[F[_], A](name: RuleName, apply: PartialFunction[(A, A), F[Facts[A] => Facts[A]]]) extends Rule[F, A]

final case class RuleN[F[_], A](name: RuleName, apply: PartialFunction[Set[A], F[Facts[A] => Facts[A]]]) extends Rule[F, A]

object Rule:
  def convert[F[_] : Functor, A, B <: A, I](action: PartialFunction[I, F[Action[B]]]): PartialFunction[I, F[Facts[A] => Facts[A]]] =
    action.map {
      case Add(add)            => (facts: Facts[A]) => facts ++ add
      case ReplaceAll(replace) => (_: Facts[A]) => Set.empty[A] ++ replace
    }
