package gh.marad.chi.language.nodes.expr.operators.bit;

import com.oracle.truffle.api.dsl.Specialization;

public abstract class ShrOperator extends BitOperator {
    @Specialization
    public long doLongs(long left, long right) {
        return left >> right;
    }
}
