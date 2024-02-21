package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Variable;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.image.NodeId;

import java.util.List;


public class ArrayHashBuiltin extends CollectionsArrayBuiltin {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var array = ChiArgs.getChiArray(frame, 0);
        return array.hashCode();
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
        return "hashCode";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.ArrayHashBuiltin;
    }
}
