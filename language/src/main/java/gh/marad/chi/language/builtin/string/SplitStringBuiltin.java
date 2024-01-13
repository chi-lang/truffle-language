package gh.marad.chi.language.builtin.string;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.Types;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.ChiArray;

import java.util.ArrayList;

public class SplitStringBuiltin extends Builtin {
    @Child
    private TruffleString.ToJavaStringNode toJava = TruffleString.ToJavaStringNode.create();
    @Child
    private TruffleString.FromJavaStringNode fromJava = TruffleString.FromJavaStringNode.create();


    @Override
    public FunctionType type() {
        return Types.fn(Types.getString(), Types.getString(), Types.getInt(), Types.array(Types.getString()));
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
        return "split";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var string = ChiArgs.getTruffleString(frame, 0);
        var splitter = ChiArgs.getTruffleString(frame, 1);
        var limit = ChiArgs.getLong(frame, 2);
        return split(toJava.execute(string), toJava.execute(splitter), (int) limit);
    }

    @CompilerDirectives.TruffleBoundary
    private ChiArray split(String s, String splitter, int limit) {
        var result = s.split(splitter, limit);
        var data = new ArrayList<>();
        for (String string : result) {
            data.add(fromJava.execute(string, TruffleString.Encoding.UTF_8));
        }
        return new ChiArray(data, Types.getString());
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.SplitStringBuiltin;
    }
}
