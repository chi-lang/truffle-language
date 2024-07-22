package gh.marad.chi.language.builtin.lang.interop.array;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.lang.interop.LangInteropBuiltin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.TODO;

public class ReadArrayElement extends LangInteropBuiltin {
    @Child
    private InteropLibrary library;

    public ReadArrayElement() {
        this.library = InteropLibrary.getFactory().createDispatched(3);
    }

    @Override
    public Function type() {
        return Type.fn(Type.getAny(), Type.getInt(), Type.getAny());
    }

    @Override
    public String name() {
        return "getArrayElement";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var receiver = ChiArgs.getObjectAndUnwrapHostSymbol(frame, 0);
        var index = ChiArgs.getLong(frame, 1);
        try {
            return library.readArrayElement(receiver, index);
        } catch (UnsupportedMessageException | InvalidArrayIndexException e) {
            throw new TODO(e);
        }
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.HasArrayElementsBuiltin;
    }
}
