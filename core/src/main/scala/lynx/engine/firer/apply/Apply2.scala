package lynx.engine.firer.apply

import lynx.rules.*

object Apply2 {
  given [F[_], A]: Apply[F, A, (A, A), Rule2[F, A]] = (rule: Rule2[F, A]) => rule.apply
}
