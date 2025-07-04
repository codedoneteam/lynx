package lynx.test

sealed trait TestFact

final case class TestFact1(x: Int) extends TestFact

final case class TestFact2(x: Int) extends TestFact
