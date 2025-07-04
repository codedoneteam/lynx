package lynx.engine.firer.apply

import lynx.tagged._

object Apply1 {
  implicit def makeApply1[F[_], A]: Apply[F, A, A, Tagged1[F, A]] = (tagged: Tagged1[F, A]) => tagged.pf
}
