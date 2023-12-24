package gh.marad.chi.language.nodes.expr.cast;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import gh.marad.chi.language.nodes.ChiNodeVisitor;

public abstract class CastToFloat extends CastExpression {
    @Specialization
    float fromLong(long value) {
        return (float) value;
    }

    @Specialization
    float fromFloat(float value) {
        return value;
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    float fromString(String value) {
        return Float.parseFloat(value);
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitCastToFloat(this);
        getValue().accept(visitor);
    }
}
