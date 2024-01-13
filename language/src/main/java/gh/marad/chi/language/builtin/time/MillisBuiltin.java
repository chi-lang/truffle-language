package gh.marad.chi.language.builtin.time;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.Types;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;

public class MillisBuiltin extends Builtin {
    @Override
    public FunctionType type() {
        return Types.fn(Types.getInt());
    }

    @Override
    public String getModuleName() {
        return "std";
    }

    @Override
    public String getPackageName() {
        return "time";
    }

    @Override
    public String name() {
        return "millis";
    }

    @Override
    public long executeLong(VirtualFrame frame) {
        return System.currentTimeMillis();
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return System.currentTimeMillis();
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.MillisBuiltin;
    }
}
