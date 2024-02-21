package gh.marad.chi.language.builtin.lang.interop.values;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.lang.interop.LangInteropBuiltin;
import gh.marad.chi.language.image.NodeId;

public class IsNullBuiltin extends LangInteropBuiltin {
    @Child
    private InteropLibrary library;

    public IsNullBuiltin() {
        this.library = InteropLibrary.getFactory().createDispatched(3);
    }

    @Override
    public Function type() {
        return Type.fn(Type.getAny(), Type.getBool());
    }

    @Override
    public String name() {
        return "isNull";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var arg = ChiArgs.getObjectAndUnwrapHostSymbol(frame, 0);
        return library.isNull(arg);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.IsNullBuiltin;
    }
}
