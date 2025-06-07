package lynx.engine.firer.state.eq

import cats.Eq

trait TupleEq:
  given [A <: AnyRef]: Eq[(A, A)] =
    (x: (A, A), y: (A, A)) =>
      (x, y) match
        case ((x1, x2), (y1, y2)) => (x1 eq y1) && (x2 eq y2)

object TupleEq extends TupleEq
