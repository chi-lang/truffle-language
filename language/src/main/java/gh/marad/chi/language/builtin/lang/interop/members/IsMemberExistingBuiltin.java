package gh.marad.chi.language.builtin.lang.interop.members;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.Types;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.lang.interop.LangInteropBuiltin;
import gh.marad.chi.language.image.NodeId;

public class IsMemberExistingBuiltin extends LangInteropBuiltin {
    @Child
    private InteropLibrary library;
    @Child
    private TruffleString.ToJavaStringNode toJavaString;

    public IsMemberExistingBuiltin() {
        this.library = InteropLibrary.getFactory().createDispatched(3);
        this.toJavaString = TruffleString.ToJavaStringNode.create();
    }

    @Override
    public FunctionType type() {
        return Types.fn(Types.getAny(), Types.getString(), Types.getBool());
    }

    @Override
    public String name() {
        return "isMemberExisting";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var receiver = ChiArgs.getObjectAndUnwrapHostSymbol(frame, 0);
        var member = ChiArgs.getTruffleString(frame, 1);
        return library.isMemberExisting(receiver, toJavaString.execute(member));
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.IsMemberExistingBuiltin;
    }
}
