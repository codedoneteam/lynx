package lynx.syntax

import cats.Functor
import cats.effect.Sync
import lynx.tagged.Mapper.{map, mapF}
import lynx.tagged._
import lynx._

trait ComposeF1Syntax {
  private type T[F[_], A] = PartialFunctionF[F, A, A]

  private def tag[F[_] : Functor, A, B <: A](pf: T[F, A]) = Tagged1[F, A](mapF[F, A, A, A](pf))

  implicit class ComposeOpsF1[F[_], A, B <: A](tags: Tags[F, A]) {
    def <+>(pf: T[F, A])(implicit functor: Functor[F]): Tags[F, A] = tags + tag(pf)
  }

  implicit class ComposePartialFunction[F[_], A, B <: A, C <: A](pf: T[F, A]) {
    def <+>(pf2: PartialFunction[A, C])(implicit sync: Sync[F]): Tags[F, A] =
      Set(tag(pf), Tagged1[F, A](map[F, A, C, A](pf2)))
  }

  implicit class ComposePartialFunction2[F[_], A, B <: A, C <: A](pf: T[F, A]) {
    def <+>(pf2: PartialFunction[(A, A), C])(implicit sync: Sync[F]): Tags[F, A] =
      Set(tag(pf), Tagged2[F, A](map[F, A, C, (A, A)](pf2)))
  }

  implicit class ComposePartialFunctionN[F[_], A, B <: A, C <: A](pf: T[F, A]) {
    def <+>(pf2: PartialFunction[Set[A], C])(implicit sync: Sync[F]): Tags[F, A] =
      Set(tag(pf), TaggedN[F, A](map[F, A, C, Set[A]](pf2)))
  }

  implicit class ComposePartialFunctionF1[F[_], A, B <: A](pf: T[F, A]) {
    def <+>(pf2: PartialFunctionF[F, A, A])(implicit sync: Sync[F]): Tags[F, A] =
      Set(tag(pf), Tagged1[F, A](mapF[F, A, A, A](pf2)))
  }

  implicit class ComposePartialFunctionF2[F[_], A, B <: A](pf: T[F, A]) {
    def <+>(pf2: PartialFunction2F[F, A, A])(implicit sync: Sync[F]): Tags[F, A] =
      Set(tag(pf), Tagged2[F, A](mapF[F, A, A, (A, A)](pf2)))
  }

  implicit class ComposePartialFunctionFN[F[_], A, B <: A](pf: T[F, A]) {
    def <+>(pf2: PartialFunctionNF[F, A, A])(implicit sync: Sync[F]): Tags[F, A] =
      Set(tag(pf), TaggedN[F, A](mapF[F, A, A, Set[A]](pf2)))
  }
}

object ComposeF1Syntax extends Compose1Syntax
