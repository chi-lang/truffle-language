package gh.marad.chi.language.nodes.value;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.nodes.ChiNodeVisitor;

public class BooleanValue extends ValueNode {
    public final boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public boolean executeBoolean(VirtualFrame frame) {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitBooleanValue(this);
    }
}
