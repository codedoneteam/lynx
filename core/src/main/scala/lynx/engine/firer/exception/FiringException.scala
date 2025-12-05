package lynx.engine.firer.exception

sealed trait FiringException

final case class MaxTimesApplyException(max: Int)
    extends Throwable(s"Tagged partial function applied maximum $max times.")
    with FiringException

final case class ApplyException(e: Throwable) extends Throwable(e.getMessage) with FiringException
