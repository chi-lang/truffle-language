package gh.marad.chi.language.nodes.expr.operators.bool;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;

@NodeChild("value")
public abstract class LogicNotOperator extends ChiNode {
    public abstract ChiNode getValue();

    @Specialization
    public boolean doBoolean(boolean value) {
        return !value;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitLogicNotOperator(this);
        getValue().accept(visitor);
    }
}
