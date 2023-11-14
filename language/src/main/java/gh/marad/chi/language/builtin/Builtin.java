package gh.marad.chi.language.builtin;

import gh.marad.chi.core.FnType;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

public abstract class Builtin extends ExpressionNode {
    public abstract FnType type();

    public abstract String getModuleName();

    public abstract String getPackageName();

    public abstract String name();
}
