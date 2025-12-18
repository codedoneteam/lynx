package lynx.engine.firer

import cats.{MonadThrow, Parallel}
import cats.implicits._
import lynx.engine.firer.apply.Apply
import lynx.engine.firer.lock.Lock
import lynx.engine.firer.supplier.Supplier
import lynx.tagged._
import lynx._

trait Firer[F[_], A, I, R <: Tagged[F, A]] {
  def fire(session: Session[F, A], refresh: Facts[A] => F[Facts[A]])(tagged: R, facts: Facts[A]): F[Facts[A]]
}

object Firer {
  def apply[F[_], A, I, R <: Tagged[F, A]](implicit firer: Firer[F, A, I, R]): Firer[F, A, I, R] = firer

  implicit def makeFirer[F[_] : MonadThrow : Parallel, A <: AnyRef, I, R <: Tagged[F, A]](implicit
    lockF: Lock[F, A, I, R],
    applyF: Apply[F, A, I, R],
    supplierF: Supplier[F, A, I]
  ): Firer[F, A, I, R] =
    new Firer[F, A, I, R] {
      override def fire(session: Session[F, A], refresh: Facts[A] => F[Facts[A]])(tagged: R, facts: Facts[A]): F[Facts[A]] =
        for {
          inputs <-
            Supplier[F, A, I]
              .supply(facts)
              .toList
              .parTraverse(input => Lock[F, A, I, R].lock(tagged, session, input).map(input -> _))
              .map(_.collect {
                case (input, false) if Apply[F, A, I, R].applyFunction(tagged).isDefinedAt(input) => input
              })

          mutated <- inputs.parTraverse(
            input =>
              Apply[F, A, I, R]
                .applyFunction(tagged)(input)
                .map(mutator => mutator(facts))
                .flatMap(refresh)
          )
        } yield mutated.toSet.flatten
    }
}
