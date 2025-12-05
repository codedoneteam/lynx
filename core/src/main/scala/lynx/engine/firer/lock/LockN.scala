package lynx.engine.firer.lock

import cats.Functor
import cats.implicits._
import lynx.tagged.{AppliedN, TaggedN}
import lynx._

object LockN {
  implicit def makeLockN[F[_] : Functor, A]: Lock[F, A, Set[A], TaggedN[F, A]] =
    (tagged: TaggedN[F, A], session: Session[F, A], input: Set[A]) =>
      session
        .getAndUpdate(_ + AppliedN(tagged, input))
        .map(_.exists {
          case AppliedN(taggedN, on) if (taggedN == tagged) && (on == input) => true
          case _                                                             => false
        })
}
