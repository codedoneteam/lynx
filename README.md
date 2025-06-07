## Lynx library

Business rules engine. Provides firing stateful rules. Rules can be combined in an independent order.

### Example

````
import cats.effect.{IO, Sync}
import cats.implicits.*
import cats.{Applicative, Semigroup}
import lynx.all.*
import lynx.types.Rules
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger


sealed trait Consideration

final case class Age(age: Int) extends Consideration

final case class Income(id: Int, income: BigDecimal) extends Consideration

final case class Children(children: Int) extends Consideration

final case class Sum(sum: BigDecimal) extends Consideration

final case class Offer(sum: BigDecimal) extends Consideration

sealed trait Extra extends Consideration

final case class ExtraSum(extra: BigDecimal) extends Extra

case object NoExtra extends Extra

sealed trait Decision extends Consideration

final case class Accept(sum: BigDecimal) extends Decision

case object Deny extends Decision

case object Manual extends Decision




object AcceptRule:
  def acceptRule[F[_] : Applicative]: Rule1[F, Consideration] =
    rule(
      "Accept rule",
      {
        case Offer(sum) => ++(Accept(sum)).pure[F]
      }
    )
    
object ChildrenRule:
  def childrenRule[F[_] : Applicative : Functor]: Rule1[F, Consideration] =
    rule(
      "Children rule",
      {
        case Children(children) => (children >= 3).pure[F].ifF(ExtraSum(1000), NoExtra).map(++(_))
      }
    )
    
object DenyRule:
  def denyRule[F[_] : Applicative]: Rule1[F, Consideration] =
    rule(
      "Deny rule",
      {
        case Age(age) if (age < 21) || (age > 70) => >>(Deny).pure[F]
      }
    )
  
object OfferRule:
  def offerRule[F[_] : Applicative]: Rule2[F, Consideration] =
    rule2(
      "Offer rule",
      {
        case (Sum(sum), ExtraSum(extra)) => ++(Offer(sum + extra)).pure[F]
        case (Sum(sum), NoExtra)         => ++(Offer(sum)).pure[F]
      }
    )
    
object SumRule:
  def sumRule[F[_] : Applicative]: RuleN[F, Consideration] =
    ruleN(
      "Sum rule",
      {
        case facts if facts.collect { case sum @ Sum(_) => sum }.isEmpty =>
          ++(domain.Sum(facts.collect { case Income(_, income) => income }.map(_ * 2).fold(BigDecimal(0))(_ + _))).pure[F]
      }
    )
                
given [F[_] : Sync]: Logger[F] = Slf4jLogger.getLoggerFromName("Logger")

given semigroup: Semigroup[Consideration] = (consideration1: Consideration, consideration2: Consideration) =>
    (consideration1, consideration2) match {
        case (_, Deny)          => Deny
        case (Deny, _)          => Deny
        case (_, Accept(sum))   => Accept(sum)
        case (consideration, _) => consideration
     }    

def rules[F[_] : Applicative]: Rules[F, Consideration] = denyRule[F] <+> offerRule[F] <+> childrenRule[F] <+> acceptRule[F] <+> sumRule[F]
 
rules[IO].fire()(Children(1), Income(0, 1500), Income(1, 500)).map(_.fold(Manual)(_ |+| _))
````

### Publish snapshot library locally

sbt clean test publish

### Release library

sbt release