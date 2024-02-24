package gh.marad.chi.language.builtin.string;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;

public class IndexOfStringBuiltin extends StringBuiltin {
    @Child
    private TruffleString.IndexOfStringNode node = TruffleString.IndexOfStringNode.create();

    @Override
    public Function type() {
        return Type.fn(Type.getString(), Type.getString(), Type.getInt(), Type.getInt(), Type.getInt());
    }
    @Override
    public String name() {
        return "indexOf";
    }

    @Override
    public long executeLong(VirtualFrame frame) {
        var haystack = ChiArgs.getTruffleString(frame, 0);
        var needle = ChiArgs.getTruffleString(frame, 1);
        var start = ChiArgs.getLong(frame, 2);
        var end = ChiArgs.getLong(frame, 3);
        return node.execute(haystack, needle, (int) start, (int) end, TruffleString.Encoding.UTF_8);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeLong(frame);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.IndexOfStringBuiltin;
    }
}
