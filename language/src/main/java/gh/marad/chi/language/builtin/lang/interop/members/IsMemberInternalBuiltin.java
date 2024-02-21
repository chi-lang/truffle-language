package gh.marad.chi.language.builtin.lang.interop.members;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.lang.interop.LangInteropBuiltin;
import gh.marad.chi.language.image.NodeId;

public class IsMemberInternalBuiltin extends LangInteropBuiltin {
    @Child
    private InteropLibrary library;
    @Child
    private TruffleString.ToJavaStringNode toJavaString;

    public IsMemberInternalBuiltin() {
        this.library = InteropLibrary.getFactory().createDispatched(3);
        this.toJavaString = TruffleString.ToJavaStringNode.create();
    }

    @Override
    public Function type() {
        return Type.fn(Type.getAny(), Type.getString(), Type.getBool());
    }

    @Override
    public String name() {
        return "isMemberInsertable";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var receiver = ChiArgs.getObjectAndUnwrapHostSymbol(frame, 0);
        var member = ChiArgs.getTruffleString(frame, 1);
        return library.isMemberInternal(receiver, toJavaString.execute(member));
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.IsMemberInternalBuiltin;
    }
}
