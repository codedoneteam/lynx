package lynx.consideration.domain

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