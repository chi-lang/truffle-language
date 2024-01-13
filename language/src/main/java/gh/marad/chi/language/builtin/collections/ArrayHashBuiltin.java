package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.TypeVariable;
import gh.marad.chi.core.types.Types;
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
    public FunctionType type() {
        var T = new TypeVariable("T");
        return new FunctionType(
                List.of(Types.array(T), Types.getInt()),
                List.of(T)
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
