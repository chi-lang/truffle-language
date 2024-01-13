package gh.marad.chi.language.builtin.string;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.Types;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;

public class SubstringBuiltin extends Builtin {
    @Child
    private TruffleString.SubstringNode node = TruffleString.SubstringNode.create();

    @Override
    public FunctionType type() {
        return Types.fn(Types.getString(), Types.getInt(), Types.getInt(), Types.getString());
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
