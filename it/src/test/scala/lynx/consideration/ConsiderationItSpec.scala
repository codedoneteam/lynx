package lynx.consideration

import lynx._
import cats.implicits._
import cats.effect.{IO, Sync}
import cats.Semigroup
import lynx.all._
import lynx.consideration.ConsiderationItSpec._
import lynx.consideration.domain._
import lynx.consideration.tagged.AcceptTagged._
import lynx.consideration.tagged.ChildrenTagged._
import lynx.consideration.tagged.DenyTagged._
import lynx.consideration.tagged.OfferTagged._
import lynx.consideration.tagged.SumTagged._
import lynx.folder.Folder
import munit.CatsEffectSuite

class ConsiderationItSpec extends CatsEffectSuite {
  test("Happy path") {
    tags[IO]
      .fire()(Children(1), Income(0, 1500), Income(1, 500))
      .value
      .assertEquals(Accept(4000).some.asRight)
  }

  test("Four children") {
    tags[IO]
      .fire()(Children(4), Income(0, 2000))
      .value
      .assertEquals(Accept(5000).some.asRight)
  }

  test("Age") {
    tags[IO]
      .fire()(Income(0, 2000), Age(18), Children(5))
      .value
      .assertEquals(Deny.some.asRight)
  }
}

object ConsiderationItSpec {
  implicit val makeFolder: Folder[Consideration, Option[Decision]] = _.fold(Manual)(_ |+| _).collectFirst {
    case decision: Decision => decision
  }

  implicit def makeFactsSemigroup: Semigroup[Consideration] =
    (consideration1: Consideration, consideration2: Consideration) =>
      (consideration1, consideration2) match {
        case (_, Deny)          => Deny
        case (Deny, _)          => Deny
        case (_, Accept(sum))   => Accept(sum)
        case (consideration, _) => consideration
      }

  def tags[F[_] : Sync]: Tags[F, Consideration] = deny <+> offer <+> accept <+> children <+> sum
}
