package lynx.engine

import cats.implicits._
import cats.{Monad, Parallel}
import lynx.engine.firer.Firer
import lynx.tagged._
import lynx._

trait Engine[F[_], A] {
  def exec(session: Session[F, A], refresh: Facts[A] => F[Facts[A]])(tags: Tags[F, A], facts: Facts[A]): F[Facts[A]]
}

object Engine {
  def apply[F[_], A](implicit engine: Engine[F, A]): Engine[F, A] = engine

  implicit def makeEngine[F[_] : Monad : Parallel, A](implicit
    firer1: Firer[F, A, A, Tagged1[F, A]],
    firer2: Firer[F, A, (A, A), Tagged2[F, A]],
    firerN: Firer[F, A, Set[A], TaggedN[F, A]]
  ): Engine[F, A] =
    new Engine[F, A] {
      override def exec(session: Session[F, A], refresh: Facts[A] => F[Facts[A]])(tags: Tags[F, A], facts: Facts[A]): F[Facts[A]] =
        tags.toList
          .parTraverse {
            case tagged1 @ Tagged1(_) =>
              Firer[F, A, A, Tagged1[F, A]]
                .fire(session, refresh)(tagged1, facts)

            case tagged2 @ Tagged2(_) =>
              Firer[F, A, (A, A), Tagged2[F, A]]
                .fire(session, refresh)(tagged2, facts)

            case taggedN @ TaggedN(_) =>
              Firer[F, A, Set[A], TaggedN[F, A]]
                .fire(session, refresh)(taggedN, facts)
          }
          .map(_.toSet.flatten)
    }
}
