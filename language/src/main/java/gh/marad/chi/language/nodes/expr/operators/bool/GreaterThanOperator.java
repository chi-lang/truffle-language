package gh.marad.chi.language.nodes.expr.operators.bool;

import com.oracle.truffle.api.dsl.Specialization;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.operators.BinaryOperatorWithFallback;

public abstract class GreaterThanOperator extends BinaryOperatorWithFallback {
    public final boolean inclusive;

    public GreaterThanOperator(boolean inclusive) {
        this.inclusive = inclusive;
    }

    @Specialization
    public boolean doLongs(long left, long right) {
        if (inclusive) {
            return left >= right;
        } else {
            return left > right;
        }
    }

    @Specialization
    public boolean doFloats(float left, float right) {
        if (inclusive) {
            return left >= right;
        } else {
            return left > right;
        }
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitGreaterThanOperator(this);
        getLeft().accept(visitor);
        getRight().accept(visitor);
    }
}
