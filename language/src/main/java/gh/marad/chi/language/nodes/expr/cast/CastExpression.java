package gh.marad.chi.language.nodes.expr.cast;

import com.oracle.truffle.api.dsl.NodeChild;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

@NodeChild(value = "value", type = ChiNode.class)
public abstract class CastExpression extends ExpressionNode {
    public abstract ChiNode getValue();
}
