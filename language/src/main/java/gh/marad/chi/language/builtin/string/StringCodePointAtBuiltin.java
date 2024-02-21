package gh.marad.chi.language.builtin.string;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;

public class StringCodePointAtBuiltin extends Builtin {
    @Child
    private TruffleString.CodePointAtIndexNode node = TruffleString.CodePointAtIndexNode.create();

    @Override
    public Function type() {
        return Type.fn(Type.getString(), Type.getInt(), Type.getInt());
    }

    @Override
    public String getModuleName() {
        return "std";
    }

    @Override
    public String getPackageName() {
        return "string";
    }

    @Override
    public String name() {
        return "codePointAt";
    }

    @Override
    public long executeLong(VirtualFrame frame) {
        var string = ChiArgs.getTruffleString(frame, 0);
        var index = ChiArgs.getLong(frame, 1);
        return node.execute(string, (int) index, TruffleString.Encoding.UTF_8);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeLong(frame);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.StringCodePointAtBuiltin;
    }
}
