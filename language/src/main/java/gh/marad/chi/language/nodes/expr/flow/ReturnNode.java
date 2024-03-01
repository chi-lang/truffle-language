package gh.marad.chi.language.nodes.expr.flow;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

public class ReturnNode extends ExpressionNode {

    private final ChiNode value;

    public ReturnNode(ChiNode value) {
        this.value = value;
    }

    public ChiNode getValue() {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        throw new ReturnException(value.executeGeneric(frame));
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitReturnNode(this);
        value.accept(visitor);
    }
}
