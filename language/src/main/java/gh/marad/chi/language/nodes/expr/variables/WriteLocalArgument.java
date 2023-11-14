package gh.marad.chi.language.nodes.expr.variables;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

@NodeChild(value = "value", type = ChiNode.class)
@NodeField(name = "slot", type = Integer.class)
public abstract class WriteLocalArgument extends ExpressionNode {
    protected abstract int getSlot();

    @Specialization
    public Object writeValue(VirtualFrame frame, Object value) {
        ChiArgs.setArgument(frame, getSlot(), value);
        return value;
    }
}
