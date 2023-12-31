package gh.marad.chi.language.nodes.function;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.Type;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.ChiFunction;

public class GetDefinedFunction extends ExpressionNode {
    public final String moduleName;
    public final String packageName;
    public final String functionName;
    public final Type[] paramTypes;
    private ChiFunction cachedFn = null;
    private Assumption functionNotRedefined = Assumption.NEVER_VALID;

    public GetDefinedFunction(String moduleName, String packageName, String functionName, Type[] paramTypes) {
        this.moduleName = moduleName;
        this.packageName = packageName;
        this.functionName = functionName;
        this.paramTypes = paramTypes;
    }

    @Override
    public ChiFunction executeFunction(VirtualFrame frame) {
        if (functionNotRedefined.isValid()) {
            return cachedFn;
        }

        var context = ChiContext.get(this);
        var module = context.modules.getOrCreateModule(moduleName);
        var lookupResult = module.findFunctionOrNull(packageName, functionName, paramTypes);
        if (lookupResult == null) {
            CompilerDirectives.transferToInterpreter();
            throw new RuntimeException("Function '%s' was not found in package %s/%s".formatted(functionName, moduleName, packageName));
        }
        cachedFn = lookupResult.function();
        functionNotRedefined = lookupResult.assumption();

        return cachedFn;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeFunction(frame);
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitGetDefinedFunction(this);
    }
}
