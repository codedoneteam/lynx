package lynx.engine.firer.state

import lynx.rules.*
import lynx.types.Session
import lynx.rules.Action.*
import lynx.rules.Applied.Applied1

object State1:
  given [F[_], A <: AnyRef]: State[F, A, A, Rule1[F, A]] with
    override def enrich(rule: Rule1[F, A], session: Session[F, A], input: A): Session[F, A] = session + Applied1(rule, input)

    override def has(rule: Rule1[F, A], session: Session[F, A], input: A): Boolean =
        session.collectFirst {
          case Applied1(rule1, on) if (rule1 == rule) && (on eq input) => rule1
        }.isDefined
