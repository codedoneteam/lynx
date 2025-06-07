package lynx.rules

enum Applied[F[_], A]:
  case Applied1(rule1: Rule1[F, A], on: A)
  
  case Applied2(rule2: Rule2[F, A], on: (A, A))
  
  case AppliedN(ruleN: RuleN[F, A], on: Set[A])
