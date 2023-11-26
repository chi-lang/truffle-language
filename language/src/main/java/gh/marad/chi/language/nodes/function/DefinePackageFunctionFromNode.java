package gh.marad.chi.language.nodes.function;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import gh.marad.chi.core.FnType;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.ChiFunction;

@NodeChild(value = "function", type = ChiNode.class)
@NodeField(name = "moduleName", type = String.class)
@NodeField(name = "packageName", type = String.class)
@NodeField(name = "functionName", type = String.class)
@NodeField(name = "type", type = FnType.class)
@NodeField(name = "isPublic", type = Boolean.class)
public abstract class DefinePackageFunctionFromNode extends ExpressionNode {

    protected abstract String getModuleName();

    protected abstract String getPackageName();

    protected abstract String getFunctionName();

    protected abstract FnType getType();

    protected abstract boolean getIsPublic();

    @Specialization
    public ChiFunction defineModuleFunction(ChiFunction function) {
        var context = ChiContext.get(this);
        var module = context.modules.getOrCreateModule(getModuleName());
        module.defineNamedFunction(getPackageName(), getFunctionName(), function, getType(), getIsPublic());
        return function;

    }
}
