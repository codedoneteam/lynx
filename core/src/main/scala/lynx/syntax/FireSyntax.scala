package lynx.syntax

import lynx._
import cats.{MonadThrow, Parallel}
import cats.data.EitherT
import cats.effect.kernel.Ref.Make
import lynx.engine.firer.Firer.makeFirer
import lynx.engine.firer.apply.Apply1.makeApply1
import lynx.engine.firer.apply.Apply2.makeApply2
import lynx.engine.firer.apply.ApplyN.makeApplyN
import lynx.engine.firer.exception.FiringException
import lynx.engine.firer.lock.Lock1.makeLock1
import lynx.engine.firer.lock.Lock2.makeLock2
import lynx.engine.firer.lock.LockN.makeLockN
import lynx.engine.firer.supplier.Supplier1.makeSupplier1
import lynx.engine.firer.supplier.Supplier2.makeSupplier2
import lynx.engine.firer.supplier.SupplierN.makeSupplierN
import lynx.folder.Folder
import lynx.runner.Runner

trait FireSyntax {
  implicit class FireOps[F[_] : MonadThrow : Make : Parallel, B, A <: AnyRef](tags: Tags[F, A]) {
    def fire(max: Int = Int.MaxValue)(facts: A*)(implicit folder: Folder[A, B]): EitherT[F, FiringException, B] =
      Runner[F, A, B].run(max)(tags, facts.toSet)
  }
}

object FireSyntax extends FireSyntax
