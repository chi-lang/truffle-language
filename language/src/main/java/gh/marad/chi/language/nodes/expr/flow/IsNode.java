package gh.marad.chi.language.nodes.expr.flow;

import com.oracle.truffle.api.dsl.Fallback;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Constraint;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.core.types.UnificationKt;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.*;

import java.util.List;

@NodeChild(value = "value", type = ChiNode.class)
@NodeField(name = "type", type = Type.class)
public abstract class IsNode extends ExpressionNode {
    public abstract Type getType();
    public abstract ChiNode getValue();

    @SuppressWarnings("unused")
    @Specialization
    public boolean doLong(long value) {
        return getType().equals(Type.getInt());
    }

    @SuppressWarnings("unused")
    @Specialization
    public boolean doBoolean(boolean value) {
        return getType().equals(Type.getBool());
    }

    @SuppressWarnings("unused")
    @Specialization
    public boolean doFloat(float value) {
        return getType().equals(Type.getFloat());
    }

    @SuppressWarnings("unused")
    @Specialization boolean doFunction(ChiFunction value) {
        throw new TODO("Comparing functions is unsupported yet!");
    }

    @SuppressWarnings("unused")
    @Specialization
    public boolean doArray(ChiArray value) {
        return typeMatches(value.getType());
    }

    @SuppressWarnings("unused")
    @Specialization
    public boolean doChiObject(ChiObject object) {
        return typeMatches(object.getType());
    }

    @SuppressWarnings("unused")
    @Specialization
    public boolean doString(TruffleString value) {
        return getType().equals(Type.getString());
    }

    @SuppressWarnings("unused")
    @Specialization
    public boolean doUnit(Unit value) {
        return getType().equals(Type.getUnit());
    }

    @SuppressWarnings("unused")
    @Fallback
    public boolean doOther(Object o) {
        return false;
    }

    private boolean typeMatches(Type type) {
        try {
            UnificationKt.unify(List.of(new Constraint(getType(), type, null)));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitIs(this);
        getValue().accept(visitor);
    }
}
