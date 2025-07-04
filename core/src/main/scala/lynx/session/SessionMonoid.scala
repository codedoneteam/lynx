package lynx.session

import cats.Monoid

object SessionMonoid {
  implicit def sessionMonoid[F[_], A]: Monoid[Session[F, A]] =
    new Monoid[Session[F, A]] {
      override def empty: Session[F, A] = Session[F, A](Set.empty)

      override def combine(session1: Session[F, A], session2: Session[F, A]): Session[F, A] =
        Session[F, A](session1.applied ++ session2.applied)
    }
}
