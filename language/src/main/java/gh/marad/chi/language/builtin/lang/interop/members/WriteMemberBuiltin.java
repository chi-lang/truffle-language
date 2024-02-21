package gh.marad.chi.language.builtin.lang.interop.members;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.core.types.Variable;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.lang.interop.LangInteropBuiltin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.TODO;

import java.util.List;

public class WriteMemberBuiltin extends LangInteropBuiltin {
    @Child
    private InteropLibrary library;
    @Child
    private TruffleString.ToJavaStringNode toJavaString;

    public WriteMemberBuiltin() {
        this.library = InteropLibrary.getFactory().createDispatched(3);
        this.toJavaString = TruffleString.ToJavaStringNode.create();
    }

    @Override
    public Function type() {
        var T = new Variable("T", 0);
        return new Function(
                List.of(Type.getAny(), Type.getString(), T, T),
                List.of("T")
        );
    }

    @Override
    public String name() {
        return "writeMember";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            var receiver = ChiArgs.getObjectAndUnwrapHostSymbol(frame, 0);
            var member = ChiArgs.getTruffleString(frame, 1);
            var value = ChiArgs.getObject(frame, 2);
            library.writeMember(receiver, toJavaString.execute(member), value);
            return value;
        } catch (UnsupportedMessageException | UnknownIdentifierException | UnsupportedTypeException e) {
            throw new TODO(e);
        }
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.WriteMemberBuiltin;
    }
}
