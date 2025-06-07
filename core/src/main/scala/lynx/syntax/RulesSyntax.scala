package lynx.syntax

import lynx.rules.*
import lynx.types.Rules

trait RulesSyntax {
  extension [F[_], A](rule1: Rule[F, A])
    infix def <+>(rule2: Rule[F, A]): Rules[F, A] = Set(rule1, rule2)

  extension [F[_], A](rules: Rules[F, A])
    infix def <+>(rule: Rule[F, A]): Rules[F, A] = rules + rule
}

object RulesSyntax extends RulesSyntax
