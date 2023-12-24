package gh.marad.chi.language.nodes.expr.operators.arithmetic;

import com.oracle.truffle.api.dsl.Specialization;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.operators.BinaryOperatorWithFallback;

public abstract class ModuloOperator extends BinaryOperatorWithFallback {
    @Specialization
    public long doLongs(long left, long right) { return Math.floorMod(left, right); }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitModuloOperator(this);
        getLeft().accept(visitor);
        getRight().accept(visitor);
    }
}
