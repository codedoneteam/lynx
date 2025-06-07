package lynx.syntax

import cats.MonadThrow
import cats.data.EitherT
import lynx.engine.firer.Firer.given
import lynx.engine.firer.apply.Apply1.given
import lynx.engine.firer.apply.Apply2.given
import lynx.engine.firer.apply.ApplyN.given
import lynx.engine.firer.exception.FiringException
import lynx.engine.firer.state.State1.given
import lynx.engine.firer.state.State2.given
import lynx.engine.firer.state.StateN.given
import lynx.engine.firer.supplier.Supplier1.given
import lynx.engine.firer.supplier.Supplier2.given
import lynx.engine.firer.supplier.SupplierN.given
import lynx.rules.Rule
import lynx.runner.Runner
import lynx.types.{Facts, Rules}
import org.typelevel.log4cats.Logger

trait FireSyntax:
  extension [F[_] : MonadThrow : Logger, A <: AnyRef](rule: Rule[F, A])
    def fireOne(max: Int = 1024)(facts: A*): EitherT[F, FiringException, Facts[A]] = Runner[F, A].run(max)(Set(rule), facts.toSet)

  extension [F[_] : MonadThrow : Logger, A <: AnyRef](rules: Rules[F, A])
    def fire(max: Int = 1024)(facts: A*): EitherT[F, FiringException, Facts[A]] = Runner[F, A].run(max)(rules, facts.toSet)

object FireSyntax extends FireSyntax
