package gh.marad.chi.language.nodes.value;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

public abstract class ValueNode extends ExpressionNode {
    @Override
    public void executeVoid(VirtualFrame frame) {
        // no need to do anything if the value is not going to be used
    }
}
