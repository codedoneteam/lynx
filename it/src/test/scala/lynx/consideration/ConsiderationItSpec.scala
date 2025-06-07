package lynx.consideration

import cats.implicits.*
import cats.effect.{IO, Sync}
import cats.{Applicative, Semigroup}
import lynx.all.*
import ConsiderationItSpec.*
import lynx.consideration.ConsiderationItSpec.given 
import lynx.consideration.domain.*
import lynx.consideration.rules.AcceptRule.acceptRule
import lynx.consideration.rules.ChildrenRule.childrenRule
import lynx.consideration.rules.DenyRule.denyRule
import lynx.consideration.rules.OfferRule.*
import lynx.consideration.rules.SumRule.sumRule
import lynx.types.Rules
import munit.CatsEffectSuite
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class ConsiderationItSpec extends CatsEffectSuite:
  test("Happy path") {
    rules[IO]
      .fire()(Children(1), Income(0, 1500), Income(1, 500))
      .map(_.fold(Manual)(_ |+| _))
      .rethrowT
      .assertEquals(Accept(4000))
  }

  test("Three children") {
    rules[IO]
      .fire()(Children(4), Income(0, 2000))
      .map(_.fold(Manual)(_ |+| _))
      .rethrowT
      .assertEquals(Accept(5000))
  }

  test("Age") {
    rules[IO]
      .fire()(Income(0, 2000), Age(18), Children(5))
      .map(_.fold(Manual)(_ |+| _))
      .rethrowT
      .assertEquals(Deny)
  }

object ConsiderationItSpec:
  given [F[_] : Sync]: Logger[F] = Slf4jLogger.getLoggerFromName("Logger")

  given Semigroup[Consideration] =
    (consideration1: Consideration, consideration2: Consideration) =>
      (consideration1, consideration2) match {
        case (_, Deny)          => Deny
        case (Deny, _)          => Deny
        case (_, Accept(sum))   => Accept(sum)
        case (consideration, _) => consideration
      }

  def rules[F[_] : Applicative]: Rules[F, Consideration] = denyRule[F] <+> offerRule[F] <+> childrenRule[F] <+> acceptRule[F] <+> sumRule[F]