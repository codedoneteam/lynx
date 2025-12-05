package lynx.syntax

import cats.effect.Sync
import lynx.tagged.Mapper.{map, mapF}
import lynx.tagged._
import lynx._

trait ComposeNSyntax {
  private type T[A, B <: A] = PartialFunction[Set[A], B]

  private def tag[F[_] : Sync, A, B <: A](pf: T[A, B]) = TaggedN[F, A](map[F, A, A, Set[A]](pf))

  implicit class ComposeOpsN[F[_], A, B <: A](tags: Tags[F, A]) {
    def <+>(pf: T[A, B])(implicit sync: Sync[F]): Tags[F, A] = tags + tag(pf)
  }

  implicit class ComposePartialFunction[F[_], A, B <: A, C <: A](pf: T[A, B]) {
    def <+>(pf2: PartialFunction[A, C])(implicit sync: Sync[F]): Tags[F, A] =
      Set(tag(pf), Tagged1[F, A](map[F, A, C, A](pf2)))
  }

  implicit class ComposePartialFunction2[F[_], A, B <: A, C <: A](pf: T[A, B]) {
    def <+>(pf2: PartialFunction[(A, A), C])(implicit sync: Sync[F]): Tags[F, A] =
      Set(tag(pf), Tagged2[F, A](map[F, A, C, (A, A)](pf2)))
  }

  implicit class ComposePartialFunctionN[F[_], A, B <: A, C <: A](pf: T[A, B]) {
    def <+>(pf2: PartialFunction[Set[A], C])(implicit sync: Sync[F]): Tags[F, A] =
      Set(tag(pf), TaggedN[F, A](map[F, A, C, Set[A]](pf2)))
  }

  implicit class ComposePartialFunctionF1[F[_], A, B <: A](pf: T[A, B]) {
    def <+>(pf2: PartialFunctionF[F, A, A])(implicit sync: Sync[F]): Tags[F, A] =
      Set(tag(pf), Tagged1[F, A](mapF[F, A, A, A](pf2)))
  }

  implicit class ComposePartialFunctionF2[F[_], A, B <: A](pf: T[A, B]) {
    def <+>(pf2: PartialFunction2F[F, A, A])(implicit sync: Sync[F]): Tags[F, A] =
      Set(tag(pf), Tagged2[F, A](mapF[F, A, A, (A, A)](pf2)))
  }

  implicit class ComposePartialFunctionFN[F[_], A, B <: A](pf: T[A, B]) {
    def <+>(pf2: PartialFunctionNF[F, A, A])(implicit sync: Sync[F]): Tags[F, A] =
      Set(tag(pf), TaggedN[F, A](mapF[F, A, A, Set[A]](pf2)))
  }
}

object ComposeNSyntax extends Compose1Syntax
