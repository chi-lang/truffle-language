package gh.marad.chi.language.nodes.expr.flow.loop;

import com.oracle.truffle.api.nodes.ControlFlowException;

public class WhileBreakException extends ControlFlowException {
    public static final WhileBreakException INSTANCE = new WhileBreakException();

    private WhileBreakException() {
    }
}
