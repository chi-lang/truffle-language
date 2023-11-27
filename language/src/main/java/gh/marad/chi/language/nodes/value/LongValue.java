package gh.marad.chi.language.nodes.value;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.nodes.ChiNodeVisitor;

public class LongValue extends ValueNode {
    public final long value;

    public LongValue(long value) {
        this.value = value;
    }

    @Override
    public long executeLong(VirtualFrame frame) {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitLongValue(this);
    }
}
