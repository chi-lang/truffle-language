package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Variable;
import gh.marad.chi.core.types.Type;
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
    public Function type() {
        var T = new Variable("T", 0);
        return new Function(
                List.of(Type.getInt(), T, Type.array(T)),
                List.of("T")
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
