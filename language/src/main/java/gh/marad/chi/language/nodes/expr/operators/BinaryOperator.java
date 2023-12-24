package gh.marad.chi.language.nodes.expr.operators;

import com.oracle.truffle.api.dsl.NodeChild;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

@NodeChild(value = "left", type = ChiNode.class)
@NodeChild(value = "right", type = ChiNode.class)
public abstract class BinaryOperator extends ExpressionNode {
    public abstract ChiNode getLeft();
    public abstract ChiNode getRight();
}
