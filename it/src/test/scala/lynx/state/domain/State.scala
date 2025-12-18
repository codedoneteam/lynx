package lynx.state.domain

import java.time.LocalDateTime

sealed trait State

case object Start extends State

final case class Wait(now: LocalDateTime, till: LocalDateTime) extends State

case object End extends State