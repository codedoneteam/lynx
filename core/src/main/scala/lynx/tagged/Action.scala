package lynx.tagged

import lynx.types._

sealed trait Action[B]

final case class Add[B](facts: Facts[B]) extends Action[B]

final case class Void[B]() extends Action[B]