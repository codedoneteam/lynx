package lynx.state.domain

import java.time.LocalDateTime

enum State:
  case Start

  case Wait(till: LocalDateTime)

  case End
