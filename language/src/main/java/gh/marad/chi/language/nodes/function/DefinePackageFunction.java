package gh.marad.chi.language.nodes.function;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.FnType;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.ChiFunction;

public class DefinePackageFunction extends ExpressionNode {
    private final String moduleName;
    private final String packageName;
    private final ChiFunction function;
    private final FnType type;

    private final boolean isPublic;

    public DefinePackageFunction(String moduleName, String packageName, ChiFunction function, FnType type, boolean isPublic) {
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
}
