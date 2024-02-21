package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.core.types.Variable;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.TODO;

import java.util.List;

public class SizeBuiltin extends CollectionsArrayBuiltin {
    @Child
    private InteropLibrary library;

    public SizeBuiltin() {
        this.library = InteropLibrary.getFactory().createDispatched(5);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var object = ChiArgs.getObject(frame, 0);
        try {
            return library.getArraySize(object);
        } catch (UnsupportedMessageException e) {
            throw new TODO(e);
        }
    }

    @Override
    public Function type() {
        var T = new Variable("T", 0);
        return new Function(
                List.of(Type.array(T), Type.getInt()),
                List.of("T")
        );
    }

    @Override
    public String name() {
        return "size";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.ArraySizeBuiltin;
    }
}
