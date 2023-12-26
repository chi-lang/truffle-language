package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.FnType;
import gh.marad.chi.core.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.Unit;

import java.util.List;

import static gh.marad.chi.core.Type.typeParameter;

public class ArrayAddAtBuiltin extends CollectionsArrayBuiltin {
    @Override
    public FnType type() {
        var T = typeParameter("T");
        return Type.genericFn(
                List.of(T),        // generic type parameters
                Type.getUnit(),    // return type
                Type.array(T),     // array
                Type.getIntType(), // index
                T);                // element
    }

    @Override
    public String name() {
        return "addAt";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.ArrayAddAtBuiltin;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var array = ChiArgs.getChiArray(frame, 0);
        var index = ChiArgs.getLong(frame,1);
        var element = ChiArgs.getObject(frame,2);
        array.add((int) index, element);
        return Unit.instance;
    }
}
