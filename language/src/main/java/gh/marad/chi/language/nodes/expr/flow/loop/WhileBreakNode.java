package gh.marad.chi.language.nodes.expr.flow.loop;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

public class WhileBreakNode extends ExpressionNode {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        throw WhileBreakException.INSTANCE;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitWhileBreakNode(this);
    }
}
