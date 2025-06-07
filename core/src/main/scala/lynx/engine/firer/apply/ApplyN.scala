package lynx.engine.firer.apply

import lynx.rules.*

object ApplyN {
  given [F[_], A]: Apply[F, A, Set[A], RuleN[F, A]] = (rule: RuleN[F, A]) => rule.apply
}
