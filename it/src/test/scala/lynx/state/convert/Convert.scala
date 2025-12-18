package lynx.state.convert

import java.time.{Instant, LocalDateTime, ZoneId}

object Convert {
  def toLocalDateTime(millis: Long): LocalDateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime
}
