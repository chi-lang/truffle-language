package gh.marad.chi.language.builtin.lang.interop.members;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.lang.interop.LangInteropBuiltin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.TODO;
import gh.marad.chi.language.runtime.Unit;

public class RemoveMemberBuiltin extends LangInteropBuiltin {
    @Child
    private InteropLibrary library;
    @Child
    private TruffleString.ToJavaStringNode toJavaString;

    public RemoveMemberBuiltin() {
        this.library = InteropLibrary.getFactory().createDispatched(3);
        this.toJavaString = TruffleString.ToJavaStringNode.create();
    }

    @Override
    public Function type() {
        return Type.fn(Type.getAny(), Type.getString(), Type.getUnit());
    }

    @Override
    public String name() {
        return "removeMember";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            var receiver = ChiArgs.getObjectAndUnwrapHostSymbol(frame, 0);
            var member = ChiArgs.getTruffleString(frame, 1);
            library.removeMember(receiver, toJavaString.execute(member));
            return Unit.instance;
        } catch (UnsupportedMessageException | UnknownIdentifierException e) {
            throw new TODO(e);
        }
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.RemoveMemberBuiltin;
    }
}
