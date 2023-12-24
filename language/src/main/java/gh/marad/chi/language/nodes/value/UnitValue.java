package gh.marad.chi.language.nodes.value;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.runtime.Unit;

public class UnitValue extends ValueNode {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return Unit.instance;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitUnitValue(this);
    }
}
