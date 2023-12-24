package gh.marad.chi.language.builtin.lang.interop.members;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.*;
import gh.marad.chi.core.FnType;
import gh.marad.chi.core.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.lang.interop.LangInteropBuiltin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.TODO;

public class NewInstanceBuiltin extends LangInteropBuiltin {
    @Child
    private InteropLibrary library;

    public NewInstanceBuiltin() {
        this.library = InteropLibrary.getFactory().createDispatched(3);
    }

    @Override
    public FnType type() {
        return Type.fn(Type.getAny(), Type.getAny(), Type.array(Type.getAny()));
    }

    @Override
    public String name() {
        return "newInstance";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            var receiver = ChiArgs.getObjectAndUnwrapHostSymbol(frame, 0);
            var argsArray = ChiArgs.getObject(frame, 1);
            var argsArraySize = (int) library.getArraySize(argsArray);
            var args = new Object[argsArraySize];
            for (var i = 0; i < argsArraySize; i++) {
                args[i] = library.readArrayElement(argsArray, i);
            }
            return library.instantiate(receiver, args);
        } catch (UnsupportedMessageException
                 | InvalidArrayIndexException
                 | UnsupportedTypeException
                 | ArityException e) {
            throw new TODO(e);
        }
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.NewInstanceBuiltin;
    }
}
