package gh.marad.chi.language.builtin.string;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.ChiArray;

public class StringCodePointsBuiltin extends StringBuiltin {
    @Child
    private TruffleString.CodePointLengthNode codePointLength = TruffleString.CodePointLengthNode.create();
    @Child
    private TruffleString.CreateCodePointIteratorNode node = TruffleString.CreateCodePointIteratorNode.create();

    @Override
    public Function type() {
        return Type.fn(Type.getString(), Type.array(Type.getInt()));
    }

    @Override
    public String name() {
        return "codePoints";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var string = ChiArgs.getTruffleString(frame, 0);
        var length = codePointLength.execute(string, TruffleString.Encoding.UTF_8);
        var iterator = node.execute(string, TruffleString.Encoding.UTF_8);
        var data = new Long[length];
        var index = 0;
        while (iterator.hasNext()) {
            data[index++] = (long) iterator.nextUncached();
        }
        return new ChiArray(data, Type.getInt());
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.StringCodePointsBuiltin;
    }
}
