package lynx.syntax

import cats.{MonadThrow, Parallel, Show}
import cats.data.EitherT
import cats.effect.kernel.Ref.Make
import lynx.engine.firer.Firer.makeFirer
import lynx.engine.firer.apply.Apply1.makeApply1
import lynx.engine.firer.apply.Apply2.makeApply2
import lynx.engine.firer.apply.ApplyN.makeApplyN
import lynx.engine.firer.exception.FiringException
import lynx.engine.firer.state.State1.makeState1
import lynx.engine.firer.state.State2.makeState2
import lynx.engine.firer.state.StateN.makeStateN
import lynx.engine.firer.supplier.Supplier1.makeSupplier1
import lynx.engine.firer.supplier.Supplier2.makeSupplier2
import lynx.engine.firer.supplier.SupplierN.makeSupplierN
import lynx.folder.Folder
import lynx.tagged.Tagged
import lynx.runner.Runner
import lynx.types.Tags

trait FireSyntax {
  implicit class FireOneOps[F[_] : MonadThrow : Make : Parallel, B, A <: AnyRef](tagged: Tagged[F, A]) {
    def fireOne[P : Show](pid: P, max: Int = 1024)(
      facts: A*
    )(implicit folder: Folder[A, B]): EitherT[F, FiringException, B] =
      Runner[F, A, B, P].run(pid, max)(Set(tagged), facts.toSet)
  }

  implicit class FireOps[F[_] : MonadThrow : Make : Parallel, B, A <: AnyRef](tags: Tags[F, A]) {
    def fire[P : Show](pid: P, max: Int = 1024)(
      facts: A*
    )(implicit folder: Folder[A, B]): EitherT[F, FiringException, B] =
      Runner[F, A, B, P].run(pid, max)(tags, facts.toSet)
  }
}

object FireSyntax extends FireSyntax
