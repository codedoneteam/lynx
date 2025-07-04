package lynx.engine.firer.state

import lynx.tagged._
import lynx.session.Session

object State1 {
  implicit def makeState1[F[_], A]: State[F, A, A, Tagged1[F, A]] =
    new State[F, A, A, Tagged1[F, A]] {
      override def enrich(tagged1: Tagged1[F, A], session: Session[F, A], input: A): Session[F, A] =
        Session[F, A](session.applied + Applied1(tagged1, input))

      override def has(tagged: Tagged1[F, A], session: Session[F, A], input: A): Boolean =
        session.applied.collectFirst {
          case Applied1(tagged1, on) if (tagged1 == tagged) && (on == input) => tagged1
        }.isDefined
    }
}
