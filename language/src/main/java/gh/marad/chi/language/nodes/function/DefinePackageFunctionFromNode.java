package gh.marad.chi.language.nodes.function;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import gh.marad.chi.core.FnType;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.ChiFunction;

@NodeChild(value = "function", type = ChiNode.class)
@NodeField(name = "moduleName", type = String.class)
@NodeField(name = "packageName", type = String.class)
@NodeField(name = "functionName", type = String.class)
@NodeField(name = "type", type = FnType.class)
@NodeField(name = "isPublic", type = Boolean.class)
public abstract class DefinePackageFunctionFromNode extends ExpressionNode {

    public abstract ChiNode getFunction();

    public abstract String getModuleName();

    public abstract String getPackageName();

    public abstract String getFunctionName();

    public abstract FnType getType();

    public abstract boolean getIsPublic();

    @Specialization
    public ChiFunction defineModuleFunction(ChiFunction function) {
        var context = ChiContext.get(this);
        var module = context.modules.getOrCreateModule(getModuleName());
        module.defineNamedFunction(getPackageName(), getFunctionName(), function, getType(), getIsPublic());
        return function;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitDefinePackageFunction(this);
        getFunction().accept(visitor);
    }
}
