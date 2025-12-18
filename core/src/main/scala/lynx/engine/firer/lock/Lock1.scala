package lynx.engine.firer.lock

import cats.Functor
import lynx.tagged._
import cats.implicits._
import lynx._

object Lock1 {
  implicit def makeLock1[F[_] : Functor, A]: Lock[F, A, A, Tagged1[F, A]] =
    (tagged: Tagged1[F, A], session: Session[F, A], input: A) =>
      session
        .getAndUpdate(_ + Applied1(tagged, input))
        .map(_.exists {
          case Applied1(tagged1, on) if (tagged1 == tagged) && (on == input) => true
          case _                                                             => false
        })
}
