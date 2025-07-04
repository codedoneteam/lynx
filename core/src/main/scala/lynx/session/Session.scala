package lynx.session

import lynx.tagged.Applied

final case class Session[F[_], A](applied: Set[Applied[F, A]])