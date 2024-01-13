package gh.marad.chi.language.nodes.expr.flow;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import gh.marad.chi.core.types.ProductType;
import gh.marad.chi.core.types.SimpleType;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.ChiObject;

@NodeChild(value = "value", type = ChiNode.class)
@NodeField(name = "typeName", type = String.class)
public abstract class IsNode extends ExpressionNode {
    public abstract String getTypeName();
    public abstract ChiNode getValue();

    @Specialization
    public boolean doChiObject(ChiObject object) {
        var type = object.getType();
        String name = null;
        if (type instanceof SimpleType t) {
            name = t.getName();
        } else if (type instanceof ProductType t) {
            name = t.getName();
        }
        return getTypeName().equals(name);
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitIs(this);
        getValue().accept(visitor);
    }
}
