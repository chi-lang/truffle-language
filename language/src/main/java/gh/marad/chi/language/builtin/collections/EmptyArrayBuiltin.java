package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Variable;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.ChiArray;

import java.util.List;


public class EmptyArrayBuiltin extends CollectionsArrayBuiltin {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return new ChiArray(Type.getAny());
    }

    @Override
    public Function type() {
        var T = new Variable("T", 0);
        return new Function(
                List.of(Type.array(T)),
                List.of("T")
        );
    }

    @Override
    public String name() {
        return "emptyArray";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.EmptyArrayBuiltin;
    }
}
