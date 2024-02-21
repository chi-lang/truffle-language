package gh.marad.chi.language.builtin.time;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;

public class MillisBuiltin extends Builtin {
    @Override
    public Function type() {
        return Type.fn(Type.getInt());
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
