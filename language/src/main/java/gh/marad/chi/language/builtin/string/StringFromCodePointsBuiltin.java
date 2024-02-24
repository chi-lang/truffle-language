package gh.marad.chi.language.builtin.string;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiTypesGen;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;

public class StringFromCodePointsBuiltin extends StringBuiltin {
    @Child
    private TruffleString.FromJavaStringNode node = TruffleString.FromJavaStringNode.create();

    @Override
    public Function type() {
        return Type.fn(Type.array(Type.getInt()), Type.getString());
    }

    @Override
    public String name() {
        return "fromCodePoints";
    }

    @Override
    public TruffleString executeString(VirtualFrame frame) {
        var codePointArray = ChiArgs.getChiArray(frame, 0);
        var objects = codePointArray.getUnderlayingArrayList();
        var codePoints = new int[objects.size()];
        for (int i = 0; i < objects.size(); i++) {
            codePoints[i] = (int) ChiTypesGen.asImplicitLong(objects.get(i));
        }
        var s = makeString(codePoints);
        return node.execute(s, TruffleString.Encoding.UTF_8);
    }

    @CompilerDirectives.TruffleBoundary
    private String makeString(int[] codePoints) {
        return new String(codePoints, 0, codePoints.length);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeString(frame);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.StringFromCodePointsBuiltin;
    }
}
