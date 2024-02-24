package gh.marad.chi.language.builtin.string;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;

public class StringLengthBuiltin extends StringBuiltin {
    @Child
    private TruffleString.CodePointLengthNode node = TruffleString.CodePointLengthNode.create();

    @Override
    public Function type() {
        return Type.fn(Type.getString(), Type.getInt());
    }

    @Override
    public String name() {
        return "length";
    }

    @Override
    public long executeLong(VirtualFrame frame) {
        return node.execute(ChiArgs.getTruffleString(frame, 0), TruffleString.Encoding.UTF_8);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeLong(frame);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.StringLengthBuiltin;
    }
}
