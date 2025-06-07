package lynx.rules

import lynx.types.*

enum Action[B]:
  case Add(facts: Facts[B])

  case ReplaceAll(facts: Facts[B])