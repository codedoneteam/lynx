package lynx.runner

import cats.implicits._
import cats.{MonadThrow, Parallel}
import cats.data.EitherT
import cats.effect.Ref
import cats.effect.kernel.Ref.Make
import lynx.engine.Engine
import lynx.engine.firer.Firer
import lynx.engine.firer.exception.{ApplyException, FiringException, MaxTimesApplyException}
import lynx.folder.Folder
import lynx.tagged.{Applied, Tagged1, Tagged2, TaggedN}
import lynx._

trait Runner[F[_], A, B] {
  def run(max: Int)(tags: Tags[F, A], facts: Facts[A]): EitherT[F, FiringException, B]
}

object Runner {
  def apply[F[_], A, B](implicit runner: Runner[F, A, B]): Runner[F, A, B] = runner

  implicit def makeRunner[F[_] : MonadThrow : Make : Parallel, A, B](implicit
    folder: Folder[A, B],
    firer1: Firer[F, A, A, Tagged1[F, A]],
    firer2: Firer[F, A, (A, A), Tagged2[F, A]],
    firerN: Firer[F, A, Set[A], TaggedN[F, A]]
  ): Runner[F, A, B] =
    new Runner[F, A, B] {
      override def run(max: Int)(tags: Tags[F, A], facts: Facts[A]): EitherT[F, FiringException, B] =
        process(max, tags, facts).attemptT
          .leftMap[FiringException] {
            case e: MaxTimesApplyException => e
            case e                         => ApplyException(e)
          }

      private def process(max: Int, tags: Tags[F, A], facts: Facts[A]): F[B] =
        for {
          counter  <- Ref.of(0)
          session  <- Ref.of(Set.empty[Applied[F, A]])
          factsRef <- Ref.of(facts)
          _        <- Engine[F, A].exec(session, refresh(counter, session, factsRef, tags, max))(tags, facts)
          result   <- factsRef.get
          folded = Folder[A, B].fold(result)
        } yield folded

      private def refresh(
        counter: Counter[F],
        session: Session[F, A],
        factsRef: Ref[F, Facts[A]],
        tags: Tags[F, A],
        max: Int
      ): Facts[A] => F[Facts[A]] =
        (facts: Facts[A]) =>
          factsRef
            .updateAndGet(_ ++ facts)
            .flatMap { facts =>
              counter.get
                .map(_ <= max)
                .ifM(
                  counter.update(_ + 1) *> Engine[F, A]
                    .exec(session, refresh(counter, session, factsRef, tags, max))(tags, facts),
                  MonadThrow[F].raiseError(MaxTimesApplyException(max))
                )
            }
    }
}
