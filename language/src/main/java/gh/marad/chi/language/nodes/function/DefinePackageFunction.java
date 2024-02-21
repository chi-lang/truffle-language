package gh.marad.chi.language.nodes.function;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.ChiFunction;

public class DefinePackageFunction extends ExpressionNode {
    private final String moduleName;
    private final String packageName;
    private final ChiFunction function;
    private final Function type;

    private final boolean isPublic;

    public DefinePackageFunction(String moduleName, String packageName, ChiFunction function, Function type, boolean isPublic) {
        this.moduleName = moduleName;
        this.packageName = packageName;
        this.function = function;
        this.type = type;
        this.isPublic = isPublic;
    }

    @Override
    public ChiFunction executeFunction(VirtualFrame frame) {
        var context = ChiContext.get(this);
        var module = context.modules.getOrCreateModule(moduleName);
        module.defineFunction(packageName, function, type, isPublic);
        return function;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeFunction(frame);
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitDefinePackageFunction(this);
    }
}
