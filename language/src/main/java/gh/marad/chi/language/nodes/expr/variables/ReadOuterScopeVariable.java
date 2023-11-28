package gh.marad.chi.language.nodes.expr.variables;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

public class ReadOuterScopeVariable extends ExpressionNode {
    public final String name;

    public ReadOuterScopeVariable(String name) {
        this.name = name;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var currentScope = getParentScope(frame);
        Object result;

        while(true) {
            result = currentScope.getValue(name);
            if (result != null) {
                return result;
            }
            currentScope = currentScope.getParentScope();
            if (currentScope == null) {
                CompilerDirectives.transferToInterpreter();
                throw new RuntimeException("Variable %s cannot be found in the outer scopes".formatted(name));
            }
        }

    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitReadOuterScopeVariable(this);
    }
}
