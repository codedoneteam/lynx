package lynx.engine.firer.state

import cats.syntax.all.*
import lynx.rules.*
import lynx.types.Session
import lynx.engine.firer.state.eq.TupleEq.given
import lynx.rules.Applied.Applied2

object State2:
  given [F[_], A <: AnyRef]: State[F, A, (A, A), Rule2[F, A]] with
    override def enrich(rule: Rule2[F, A], session: Session[F, A], input: (A, A)): Session[F, A] = session + Applied2(rule, input)

    override def has(rule: Rule2[F, A], session: Session[F, A], input: (A, A)): Boolean =
        session.collectFirst {
          case Applied2(rule2, on) if (rule2 == rule) && (on === input) => rule
        }.isDefined