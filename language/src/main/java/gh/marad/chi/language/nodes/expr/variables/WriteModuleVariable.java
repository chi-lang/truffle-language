package gh.marad.chi.language.nodes.expr.variables;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

@NodeField(name = "moduleName", type = String.class)
@NodeField(name = "packageName", type = String.class)
@NodeField(name = "variableName", type = String.class)
@NodeChild(value = "value", type = ChiNode.class)
public abstract class WriteModuleVariable extends ExpressionNode {
    public abstract String getModuleName();

    public abstract String getPackageName();

    public abstract String getVariableName();

    public abstract ChiNode getValue();

    @Specialization
    public Object saveObject(Object value) {
        var ctx = ChiContext.get(this);
        ctx.modules.getOrCreateModule(getModuleName())
                   .defineVariable(getPackageName(), getVariableName(), value);
        return value;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitWriteModuleVariable(this);
        getValue().accept(visitor);
    }
}
