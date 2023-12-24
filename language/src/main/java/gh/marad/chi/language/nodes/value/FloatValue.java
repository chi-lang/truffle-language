package gh.marad.chi.language.nodes.value;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.nodes.ChiNodeVisitor;

public class FloatValue extends ValueNode {
    public final float value;

    public FloatValue(float value) {
        this.value = value;
    }

    @Override
    public float executeFloat(VirtualFrame frame) {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitFloatValue(this);
    }
}
