package lynx.syntax

import cats.Functor
import lynx.types.*
import lynx.rules.*
import lynx.rules.Action.{Add, ReplaceAll}
import lynx.rules.Rule.convert

trait RuleSyntax:
  infix def ++[B](fact: Fact[B]): Action[Fact[B]] = Add(Set(fact))

  infix def +++[B](facts: Fact[B]*): Action[Fact[B]] = Add(facts.toSet)

  infix def >>[B](fact: Fact[B]): Action[Fact[B]] = ReplaceAll(Set(fact))

  infix def >>>[B](facts: Fact[B]*): Action[Fact[B]] = ReplaceAll(facts.toSet)

  def rule[F[_] : Functor, A, B <: A](ruleName: RuleName, apply: PartialFunction[Fact[A], F[Action[B]]]): Rule1[F, A] =
    Rule1[F, A](ruleName, convert[F, A, B, A](apply))

  def rule2[F[_] : Functor, A, B <: A](ruleName: RuleName, apply: PartialFunction[(Fact[A], Fact[A]), F[Action[B]]]): Rule2[F, A] =
    Rule2[F, A](ruleName, convert[F, A, B, (A, A)](apply))

  def ruleN[F[_] : Functor, A, B <: A](ruleName: RuleName, apply: PartialFunction[Facts[A], F[Action[B]]]): RuleN[F, A] =
    RuleN[F, A](ruleName, convert[F, A, B, Facts[A]](apply))

object RuleSyntax extends RuleSyntax
