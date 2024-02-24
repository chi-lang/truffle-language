package gh.marad.chi.language.builtin.string;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;

public class SubstringBuiltin extends StringBuiltin {
    @Child
    private TruffleString.SubstringNode node = TruffleString.SubstringNode.create();

    @Override
    public Function type() {
        return Type.fn(Type.getString(), Type.getInt(), Type.getInt(), Type.getString());
    }

    @Override
    public String name() {
        return "substring";
    }

    @Override
    public TruffleString executeString(VirtualFrame frame) {
        var string = ChiArgs.getTruffleString(frame, 0);
        var start = ChiArgs.getLong(frame, 1);
        var length = ChiArgs.getLong(frame, 2);
        return node.execute(string, (int) start, (int) length, TruffleString.Encoding.UTF_8, false);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeString(frame);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.SubstringBuiltin;
    }
}
