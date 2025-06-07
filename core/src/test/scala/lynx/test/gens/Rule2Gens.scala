package lynx.test.gens

import cats.implicits.*
import cats.{Applicative, Functor}
import lynx.all.rule2
import lynx.rules.*
import lynx.syntax.RuleSyntax.{>>, rule}
import lynx.test.gens.TestFactsGens.given
import lynx.test.TestFact
import lynx.test.TestFact.*
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

object Rule2Gens:
  given [F[_] : Applicative : Functor]: Arbitrary[Rule2[F, TestFact]] = Arbitrary(rule2Gen)
  
  def rule2Gen[F[_] : Applicative : Functor]: Gen[Rule2[F, TestFact]] =
    for {
      name      <- arbitrary[String]
      testFact1 <- arbitrary[TestFact1]
    } yield rule2(
      name,
      {
        case _ => testFact1.pure[F].map(>>)
      }
    )