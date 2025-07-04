package lynx.engine.firer.state

import lynx.tagged._
import lynx.session.Session

object State2 {
  implicit def makeState2[F[_], A]: State[F, A, (A, A), Tagged2[F, A]] =
    new State[F, A, (A, A), Tagged2[F, A]] {
      override def enrich(tagged2: Tagged2[F, A], session: Session[F, A], input: (A, A)): Session[F, A] =
        Session[F, A](session.applied + Applied2(tagged2, input))

      override def has(tagged2: Tagged2[F, A], session: Session[F, A], input: (A, A)): Boolean =
        session.applied.collectFirst {
          case Applied2(tagged2, on) if (tagged2 == tagged2) && (on == input) => tagged2
        }.isDefined
    }
}
