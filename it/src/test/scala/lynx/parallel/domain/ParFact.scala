package lynx.parallel.domain

sealed trait ParFact

final case class Start(x: Int) extends ParFact

final case class ParFact1(x: Int) extends ParFact

final case class ParFact2(x: Int) extends ParFact

final case class ParFact3(x: Int) extends ParFact
