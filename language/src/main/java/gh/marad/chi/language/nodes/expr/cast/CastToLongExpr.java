package gh.marad.chi.language.nodes.expr.cast;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import gh.marad.chi.language.nodes.ChiNodeVisitor;

public abstract class CastToLongExpr extends CastExpression {

    @Specialization
    long fromInt(int value) {
        return value;
    }

    @Specialization
    long fromLong(long value) {
        return value;
    }

    @Specialization
    long fromFloat(float value) {
        return (long) value;
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    long fromString(String value) {
        return Long.parseLong(value);
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitCastToLongExpr(this);
        getValue().accept(visitor);
    }
}
