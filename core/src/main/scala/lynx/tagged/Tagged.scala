package lynx.tagged

import lynx._

sealed trait Tagged[F[_], A]

final case class Tagged1[F[_], A](pf: PartialFunction[A, F[Facts[A] => Facts[A]]]) extends Tagged[F, A]

final case class Tagged2[F[_], A](pf: PartialFunction[(A, A), F[Facts[A] => Facts[A]]]) extends Tagged[F, A]

final case class TaggedN[F[_], A](pf: PartialFunction[Set[A], F[Facts[A] => Facts[A]]]) extends Tagged[F, A]
