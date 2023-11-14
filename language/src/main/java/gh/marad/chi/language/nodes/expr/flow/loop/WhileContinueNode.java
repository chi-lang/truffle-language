package gh.marad.chi.language.nodes.expr.flow.loop;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

public class WhileContinueNode extends ExpressionNode {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        throw WhileContinueException.INSTANCE;
    }
}
