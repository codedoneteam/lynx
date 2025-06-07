package lynx.test.gens

import cats.implicits.*
import cats.{Applicative, Functor}
import lynx.all.ruleN
import lynx.rules.*
import lynx.syntax.RuleSyntax.{>>, rule}
import lynx.test.gens.TestFactsGens.given
import lynx.test.TestFact
import lynx.test.TestFact.*
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

object RuleNGens:
  given [F[_] : Applicative : Functor]: Arbitrary[RuleN[F, TestFact]] = Arbitrary(ruleNGen)
  
  def ruleNGen[F[_] : Applicative : Functor]: Gen[RuleN[F, TestFact]] =
    for {
      name      <- arbitrary[String]
      testFact1 <- arbitrary[TestFact1]
    } yield ruleN(
      name,
      {
        case _ => testFact1.pure[F].map(>>)
      }
    )