package lynx.runner

import cats.implicits._
import cats.{MonadThrow, Monoid, Parallel, Show}
import cats.data.EitherT
import cats.effect.kernel.Ref
import cats.effect.kernel.Ref.Make
import lynx.engine.Engine
import lynx.engine.firer.Firer
import lynx.engine.firer.exception.{ApplyException, FiringException, MaxTimesApplyException}
import lynx.folder.Folder
import lynx.tagged.{Tagged1, Tagged2, TaggedN}
import lynx.session.Session
import lynx.session.SessionMonoid.sessionMonoid
import lynx.types.{Facts, Tags}

trait Runner[F[_], A, B, P] {
  def run(pid: P, max: Int)(tags: Tags[F, A], facts: Facts[A]): EitherT[F, FiringException, B]
}

object Runner {
  def apply[F[_], A, B, P](implicit runner: Runner[F, A, B, P]): Runner[F, A, B, P] = runner

  implicit def makeRunner[F[_] : MonadThrow : Make : Parallel, A, B, P : Show](implicit
    folder: Folder[A, B],
    firer1: Firer[F, A, A, Tagged1[F, A], P],
    firer2: Firer[F, A, (A, A), Tagged2[F, A], P],
    firerN: Firer[F, A, Set[A], TaggedN[F, A], P]
  ): Runner[F, A, B, P] =
    new Runner[F, A, B, P] {
      override def run(pid: P, max: Int)(tags: Tags[F, A], facts: Facts[A]): EitherT[F, FiringException, B] =
        Ref
          .of(Monoid[Session[F, A]].empty)
          .flatMap(Engine[F, A, P].exec(_)(pid, tags, facts, 0, max))
          .map(Folder[A, B].fold(_))
          .attemptT
          .leftMap[FiringException] {
            case e: MaxTimesApplyException => e
            case e                         => ApplyException(e)
          }
    }
}
