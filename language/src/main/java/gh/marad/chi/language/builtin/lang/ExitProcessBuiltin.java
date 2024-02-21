package gh.marad.chi.language.builtin.lang;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.Unit;

public class ExitProcessBuiltin extends Builtin {

    public static ExitProcessBuiltin instance = new ExitProcessBuiltin();

    private ExitProcessBuiltin() {}

    @Override
    public Function type() {
        return Type.fn(Type.getInt(), Type.getUnit());
    }

    @Override
    public String getModuleName() {
        return "std";
    }

    @Override
    public String getPackageName() {
        return "lang";
    }

    @Override
    public String name() {
        return "exitProcess";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.ExitProcessBuiltin;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var status = ChiArgs.getLong(frame, 0);
        CompilerDirectives.transferToInterpreterAndInvalidate();
        System.exit((int)status);
        return Unit.instance;
    }
}
