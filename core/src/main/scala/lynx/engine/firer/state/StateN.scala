package lynx.engine.firer.state

import lynx.tagged.{AppliedN, TaggedN}
import lynx.session.Session

object StateN {
  implicit def makeStateN[F[_], A]: State[F, A, Set[A], TaggedN[F, A]] =
    new State[F, A, Set[A], TaggedN[F, A]] {
      override def enrich(taggedN: TaggedN[F, A], session: Session[F, A], input: Set[A]): Session[F, A] =
        Session[F, A](session.applied + AppliedN(taggedN, input))

      override def has(taggedN: TaggedN[F, A], session: Session[F, A], input: Set[A]): Boolean =
        session.applied.collectFirst {
          case AppliedN(tagged, on) if (tagged == taggedN) && (on == input) => taggedN
        }.isDefined
    }
}
