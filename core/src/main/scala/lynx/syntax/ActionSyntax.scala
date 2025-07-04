package lynx.syntax

import lynx.types._
import lynx.tagged._

trait ActionSyntax {
  def ++[B](fact: Fact[B]): Action[Fact[B]] = Add(Set(fact))

  def +++[B](facts: Fact[B]*): Action[Fact[B]] = Add(facts.toSet)

  def *>>[B](fact: Fact[B]): Action[Fact[B]] = Void()
}

object ActionSyntax extends ActionSyntax
