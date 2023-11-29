package gh.marad.chi.language.nodes.expr.operators.bool;

import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;

public class LogicAndOperator extends ShortCircuitBaseLogicOperator {

    public LogicAndOperator(ChiNode left, ChiNode right) {
        super(left, right);
    }

    @Override
    protected boolean shouldEvaluateRight(boolean leftValue) {
        return leftValue;
    }

    @Override
    protected boolean execute(boolean leftValue, boolean rightValue) {
        return leftValue && rightValue;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitLogicAndOperator(this);
        getLeft().accept(visitor);
        getRight().accept(visitor);
    }
}
