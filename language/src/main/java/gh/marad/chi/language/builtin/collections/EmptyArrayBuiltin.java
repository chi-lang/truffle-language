package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.TypeVariable;
import gh.marad.chi.core.types.Types;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.ChiArray;

import java.util.List;


public class EmptyArrayBuiltin extends CollectionsArrayBuiltin {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return new ChiArray(Types.getAny());
    }

    @Override
    public FunctionType type() {
        var T = new TypeVariable("T");
        return new FunctionType(
                List.of(Types.array(T)),
                List.of(T)
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
