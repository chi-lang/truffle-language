package gh.marad.chi.language.nodes.expr.flow;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.Unit;

public class ReturnUnitNode extends ExpressionNode {
    public static ReturnUnitNode instance = new ReturnUnitNode();

    private ReturnUnitNode() {}

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        throw new ReturnException(Unit.instance);
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitReturnUnitNode(this);
    }
}
