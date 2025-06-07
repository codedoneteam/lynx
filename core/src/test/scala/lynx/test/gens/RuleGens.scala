package lynx.test.gens

import cats.implicits.*
import cats.{Applicative, Functor}
import lynx.rules.Rule1
import lynx.syntax.RuleSyntax.{>>, rule}
import lynx.test.TestFact
import lynx.test.TestFact.*
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

object RuleGens:
  given [F[_] : Applicative : Functor]: Arbitrary[Rule1[F, TestFact]] = Arbitrary(ruleGen)
  
  def ruleGen[F[_] : Applicative : Functor]: Gen[Rule1[F, TestFact]] =
    arbitrary[String].map(
      name =>
        rule(
          name,
          {
            case _ => TestFact1(1).pure[F].map(>>)
          }
        )
    )