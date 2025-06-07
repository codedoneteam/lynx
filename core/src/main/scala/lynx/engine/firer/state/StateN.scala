package lynx.engine.firer.state

import lynx.rules.*
import lynx.types.Session
import lynx.rules.Action.*
import lynx.rules.Applied.AppliedN

object StateN:
  given [F[_], A]: State[F, A, Set[A], RuleN[F, A]] with
    override def enrich(rule: RuleN[F, A], session: Session[F, A], input: Set[A]): Session[F, A] = Set(AppliedN(rule, input))

    override def has(rule: RuleN[F, A], session: Session[F, A], input: Set[A]): Boolean =
        session.collectFirst {
          case AppliedN(ruleN, on) if (ruleN == rule) && (on eq input) => rule
        }.isDefined