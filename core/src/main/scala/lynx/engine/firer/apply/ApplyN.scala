package lynx.engine.firer.apply

import lynx.tagged._

object ApplyN {
  implicit def makeApplyN[F[_], A]: Apply[F, A, Set[A], TaggedN[F, A]] = (taggedN: TaggedN[F, A]) => taggedN.pf
}
