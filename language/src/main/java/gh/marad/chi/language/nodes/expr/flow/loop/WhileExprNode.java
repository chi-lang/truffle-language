package gh.marad.chi.language.nodes.expr.flow.loop;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.Unit;


public class WhileExprNode extends ExpressionNode {
    @Child
    private LoopNode loop;

    public WhileExprNode(ChiNode condition, ChiNode loop) {
        this.loop = Truffle.getRuntime().createLoopNode(new WhileRepeatingNode(condition, loop));
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        loop.execute(frame);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        loop.execute(frame);
        return Unit.instance;
    }
}
