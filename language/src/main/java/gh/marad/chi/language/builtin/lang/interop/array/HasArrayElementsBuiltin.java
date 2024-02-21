package gh.marad.chi.language.builtin.lang.interop.array;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.lang.interop.LangInteropBuiltin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.TODO;

public class HasArrayElementsBuiltin extends LangInteropBuiltin {
    @Child
    private InteropLibrary library;

    public HasArrayElementsBuiltin() {
        this.library = InteropLibrary.getFactory().createDispatched(3);
    }

    @Override
    public Function type() {
        return Type.fn(Type.getAny(), Type.getBool(), Type.array(Type.getString()));
    }

    @Override
    public String name() {
        return "hasArrayElements";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            var receiver = ChiArgs.getObjectAndUnwrapHostSymbol(frame, 0);
            var includeInternal = ChiArgs.getBoolean(frame, 1);
            return library.getMembers(receiver, includeInternal);
        } catch (UnsupportedMessageException e) {
            throw new TODO(e);
        }
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.HasArrayElementsBuiltin;
    }
}
