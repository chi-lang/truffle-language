package gh.marad.chi.language.nodes.expr.variables;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

public class ReadLocalArgument extends ExpressionNode {
    public final int slot;

    public ReadLocalArgument(int slot) {
        this.slot = slot;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return ChiArgs.getObject(frame, slot);
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitReadLocalArgument(this);
    }
}
