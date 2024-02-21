package gh.marad.chi.language.builtin;

import gh.marad.chi.core.types.Function;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

public abstract class Builtin extends ExpressionNode {
    public abstract Function type();

    public abstract String getModuleName();

    public abstract String getPackageName();

    public abstract String name();

    public abstract NodeId getNodeId();

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitBuiltin(this);
    }
}
