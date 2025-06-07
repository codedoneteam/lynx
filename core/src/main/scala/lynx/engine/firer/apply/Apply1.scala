package lynx.engine.firer.apply

import lynx.rules.*

object Apply1:
  given [F[_], A]: Apply[F, A, A, Rule1[F, A]] = (rule: Rule1[F, A]) => rule.apply