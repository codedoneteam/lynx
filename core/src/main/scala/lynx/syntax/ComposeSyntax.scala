package lynx.syntax

trait ComposeSyntax
    extends Compose1Syntax
    with Compose2Syntax
    with ComposeNSyntax
    with ComposeF1Syntax
    with ComposeF2Syntax
    with ComposeFNSyntax

object ComposeSyntax extends ComposeSyntax
