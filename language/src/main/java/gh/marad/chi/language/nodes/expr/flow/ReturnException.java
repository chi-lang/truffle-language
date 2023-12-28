package gh.marad.chi.language.nodes.expr.flow;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class ReturnException extends ControlFlowException {
    public final Object value;

    public ReturnException(Object value) {
        this.value = value;
    }
}
