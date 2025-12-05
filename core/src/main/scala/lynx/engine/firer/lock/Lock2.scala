package lynx.engine.firer.lock

import cats.implicits._
import cats.Functor
import lynx.tagged._
import lynx._

object Lock2 {
  implicit def makeLock2[F[_] : Functor, A]: Lock[F, A, (A, A), Tagged2[F, A]] =
    (tagged: Tagged2[F, A], session: Session[F, A], input: (A, A)) =>
      session
        .getAndUpdate(_ + Applied2(tagged, input))
        .map(_.exists {
          case Applied2(tagged2, on) if (tagged2 == tagged) && (on == input) => true
          case _                                                             => false
        })
}
