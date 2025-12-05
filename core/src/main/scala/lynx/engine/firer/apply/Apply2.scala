package lynx.engine.firer.apply

import lynx.tagged._

object Apply2 {
  implicit def makeApply2[F[_], A]: Apply[F, A, (A, A), Tagged2[F, A]] = (tagged: Tagged2[F, A]) => tagged.pf
}
