package gh.marad.chi.language.nodes.expr.flow.effect;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class AbortEffectWithValueException extends ControlFlowException {
    private final Object value;

    public AbortEffectWithValueException(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
