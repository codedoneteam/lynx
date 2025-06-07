package lynx.test.gens

import cats.implicits.*
import cats.{Applicative, Functor}
import lynx.rules.Rule1
import lynx.syntax.RuleSyntax.{>>, rule}
import lynx.test.TestFact.TestFact1
import lynx.test.gens.TestFactsGens.given
import lynx.test.TestFact
import lynx.test.TestFact.*
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

object Rule1Gens:
  given [F[_] : Applicative : Functor]: Arbitrary[Rule1[F, TestFact]] = Arbitrary(rule1Gen)
  
  def rule1Gen[F[_] : Applicative : Functor]: Gen[Rule1[F, TestFact]] =
    arbitrary[String].map(
      name =>
        rule(
          name,
          {
            case TestFact1(0) => TestFact1(1).pure[F].map(>>)
          }
        )
    )