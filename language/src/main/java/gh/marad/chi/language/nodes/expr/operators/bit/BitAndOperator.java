package gh.marad.chi.language.nodes.expr.operators.bit;

import com.oracle.truffle.api.dsl.Specialization;
import gh.marad.chi.language.nodes.ChiNodeVisitor;

public abstract class BitAndOperator extends BitOperator {
    @Specialization
    public long doLongs(long left, long right) {
        return left & right;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitBitAndOperator(this);
        getLeft().accept(visitor);
        getRight().accept(visitor);
    }
}
