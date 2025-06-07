package lynx.engine.firer.exception

sealed trait FiringException extends Throwable

final case class MaxTimesApplyException(max: Int) extends Throwable(s"Rules fired maximum $max count times.") with FiringException

final case class ApplyException(e: Throwable) extends Throwable(e.getMessage) with FiringException
