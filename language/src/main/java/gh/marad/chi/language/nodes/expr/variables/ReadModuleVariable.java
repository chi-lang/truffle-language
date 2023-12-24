package gh.marad.chi.language.nodes.expr.variables;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

public class ReadModuleVariable extends ExpressionNode {
    public final String moduleName;
    public final String packageName;
    public final String variableName;

    public ReadModuleVariable(String moduleName, String packageName, String variableName) {
        this.moduleName = moduleName;
        this.packageName = packageName;
        this.variableName = variableName;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var ctx = ChiContext.get(this);
        return ctx.modules.getOrCreateModule(moduleName)
                          .findVariableFunctionOrNull(packageName, variableName);

    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitReadModuleVariable(this);
    }
}
