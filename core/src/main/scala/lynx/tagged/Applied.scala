package lynx.tagged

sealed trait Applied[F[_], A]

final case class Applied1[F[_], A](tagged1: Tagged1[F, A], on: A) extends Applied[F, A]

final case class Applied2[F[_], A](tagged2: Tagged2[F, A], on: (A, A)) extends Applied[F, A]

final case class AppliedN[F[_], A](taggedN: TaggedN[F, A], on: Set[A]) extends Applied[F, A]
