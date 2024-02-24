package gh.marad.chi.language.builtin.string;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;

public class IndexOfCodePointBuiltin extends StringBuiltin {
    @Child
    private TruffleString.IndexOfCodePointNode node = TruffleString.IndexOfCodePointNode.create();

    @Override
    public Function type() {
        return Type.fn(Type.getString(), Type.getInt(), Type.getInt(), Type.getInt(), Type.getInt());
    }

    @Override
    public String name() {
        return "indexOfCodePoint";
    }

    @Override
    public long executeLong(VirtualFrame frame) {
        var string = ChiArgs.getTruffleString(frame, 0);
        var codePoint = ChiArgs.getLong(frame, 1);
        var start = ChiArgs.getLong(frame, 2);
        var end = ChiArgs.getLong(frame, 3);
        return node.execute(string, (int) codePoint, (int) start, (int) end, TruffleString.Encoding.UTF_8);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeLong(frame);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.IndexOfCodePointBuiltin;
    }
}
