package gh.marad.chi.language.builtin.string;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;

public class ToLowerBuiltin extends StringBuiltin {
    @Child
    private TruffleString.ToJavaStringNode toJava = TruffleString.ToJavaStringNode.create();
    @Child
    private TruffleString.FromJavaStringNode fromJava = TruffleString.FromJavaStringNode.create();


    @Override
    public Function type() {
        return Type.fn(Type.getString(), Type.getString());
    }

    @Override
    public String name() {
        return "toLower";
    }

    @Override
    public TruffleString executeString(VirtualFrame frame) {
        var string = ChiArgs.getTruffleString(frame, 0);
        var javaString = toJava.execute(string);
        return fromJava.execute(toLower(javaString), TruffleString.Encoding.UTF_8);
    }

    @CompilerDirectives.TruffleBoundary
    private String toLower(String s) {
        return s.toLowerCase();
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeString(frame);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.ToLowerBuiltin;
    }
}
