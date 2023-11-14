package gh.marad.chi.language.nodes.expr.operators.arithmetic;

import com.oracle.truffle.api.dsl.Specialization;
import gh.marad.chi.language.nodes.expr.operators.BinaryOperatorWithFallback;

public abstract class MultiplyOperator extends BinaryOperatorWithFallback {
    @Specialization
    public long doLongs(long left, long right) { return Math.multiplyExact(left, right); }

    @Specialization
    public float doFloats(float left, float right) { return left * right; }
}
