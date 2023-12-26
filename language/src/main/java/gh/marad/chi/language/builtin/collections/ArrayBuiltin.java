package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.FnType;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiTypes;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.ChiArray;

import java.util.List;

import static gh.marad.chi.core.Type.*;

public class ArrayBuiltin extends CollectionsArrayBuiltin {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var capacity = ChiArgs.getLong(frame, 0);
        var defaultValue = ChiArgs.getObject(frame, 1);
        return new ChiArray((int) capacity, defaultValue, ChiTypes.getType(defaultValue));
    }

    @Override
    public FnType type() {
        var T = typeParameter("T");
        return genericFn(
                List.of(T),    // genericTypeParameters
                array(T),      // return type
                getIntType(),  // argument types...
                T);
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
