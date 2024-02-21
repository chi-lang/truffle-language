package gh.marad.chi.language.nodes.expr.flow;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import gh.marad.chi.core.types.Constraint;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.core.types.UnificationKt;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.ChiObject;

import java.util.List;

@NodeChild(value = "value", type = ChiNode.class)
@NodeField(name = "type", type = Type.class)
public abstract class IsNode extends ExpressionNode {
    public abstract Type getType();
    public abstract ChiNode getValue();

    @Specialization
    public boolean doChiObject(ChiObject object) {
        var type = object.getType();

        try {
            UnificationKt.unify(List.of(new Constraint(getType(), type)));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitIs(this);
        getValue().accept(visitor);
    }
}
