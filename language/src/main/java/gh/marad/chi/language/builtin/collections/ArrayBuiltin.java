package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.TypeVariable;
import gh.marad.chi.core.types.Types;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiTypes;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.ChiArray;

import java.util.List;


public class ArrayBuiltin extends CollectionsArrayBuiltin {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var capacity = ChiArgs.getLong(frame, 0);
        var defaultValue = ChiArgs.getObject(frame, 1);
        return new ChiArray((int) capacity, defaultValue, ChiTypes.getType(defaultValue));
    }

    @Override
    public FunctionType type() {
        var T = new TypeVariable("T");
        return new FunctionType(
                List.of(Types.getInt(), T, Types.array(T)),
                List.of(T)
        );
    }

    @Override
    public String name() {
        return "array";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.ArrayBuiltin;
    }
}
