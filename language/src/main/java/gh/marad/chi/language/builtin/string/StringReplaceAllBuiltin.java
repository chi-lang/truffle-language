package gh.marad.chi.language.builtin.string;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.Types;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;

public class StringReplaceAllBuiltin extends Builtin {
    @Child
    private TruffleString.ToJavaStringNode toJava = TruffleString.ToJavaStringNode.create();
    @Child
    private TruffleString.FromJavaStringNode fromJava = TruffleString.FromJavaStringNode.create();


    @Override
    public FunctionType type() {
        return Types.fn(Types.getString(), Types.getString(), Types.getString(), Types.getString());
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
        return "replaceAll";
    }

    @Override
    public TruffleString executeString(VirtualFrame frame) {
        var string = ChiArgs.getTruffleString(frame, 0);
        var toReplace = ChiArgs.getTruffleString(frame, 1);
        var withWhat = ChiArgs.getTruffleString(frame, 2);
        return fromJava.execute(
                replaceAll(
                        toJava.execute(string),
                        toJava.execute(toReplace),
                        toJava.execute(withWhat)
                ),
                TruffleString.Encoding.UTF_8);
    }

    @CompilerDirectives.TruffleBoundary
    public String replaceAll(String haystack, String needle, String replacement) {
        return haystack.replaceAll(needle, replacement);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeString(frame);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.StringReplaceAllBuiltin;
    }
}
