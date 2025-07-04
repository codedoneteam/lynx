package lynx.engine

import cats.implicits._
import lynx.types._
import cats.{Monad, Parallel, Show}
import cats.effect.Ref
import lynx.engine.firer.Firer
import lynx.tagged._
import lynx.session.Session
import lynx.session.SessionMonoid.sessionMonoid

trait Engine[F[_], A, P] {
  def exec(sessionRef: Ref[F, Session[F, A]])(pid: P, tags: Tags[F, A], facts: Facts[A], n: Int, max: Int): F[Facts[A]]
}

object Engine {
  def apply[F[_], A, P](implicit engine: Engine[F, A, P]): Engine[F, A, P] = engine

  implicit def makeEngine[F[_] : Monad : Parallel, A, P : Show](implicit
    firer1: Firer[F, A, A, Tagged1[F, A], P],
    firer2: Firer[F, A, (A, A), Tagged2[F, A], P],
    firerN: Firer[F, A, Set[A], TaggedN[F, A], P]
  ): Engine[F, A, P] =
    new Engine[F, A, P] {
      override def exec(sessionRef: Ref[F, Session[F, A]])(pid: P, tags: Tags[F, A], facts: Facts[A], n: Int, max: Int): F[Facts[A]] =
        for {
          fired <-
            tags.toList
              .parTraverse {
                case tagged1 @ Tagged1(_) =>
                  Firer[F, A, A, Tagged1[F, A], P]
                    .fire(sessionRef, pid, tagged1, facts, n, max)
                    .flatMap { firedFacts =>
                      (facts == firedFacts)
                        .pure[F]
                        .ifM(facts.pure[F], exec(sessionRef)(pid, tags, firedFacts, n + 1, max))
                    }

                case tagged2 @ Tagged2(_) =>
                  Firer[F, A, (A, A), Tagged2[F, A], P]
                    .fire(sessionRef, pid, tagged2, facts, n, max)
                    .flatMap { firedFacts =>
                      (facts == firedFacts)
                        .pure[F]
                        .ifM(facts.pure[F], exec(sessionRef)(pid, tags, firedFacts, n + 1, max))
                    }

                case taggedN @ TaggedN(_) =>
                  Firer[F, A, Set[A], TaggedN[F, A], P]
                    .fire(sessionRef, pid, taggedN, facts, n, max)
                    .flatMap { firedFacts =>
                      (facts == firedFacts)
                        .pure[F]
                        .ifM(facts.pure[F], exec(sessionRef)(pid, tags, firedFacts, n + 1, max))
                    }
              }
              .map(_.toSet.flatten)

          result <- (fired == facts).pure[F].ifM(facts.pure[F], exec(sessionRef)(pid, tags, fired, n + 1, max))
        } yield result
    }
}
