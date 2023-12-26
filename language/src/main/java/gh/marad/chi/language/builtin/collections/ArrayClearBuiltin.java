package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.FnType;
import gh.marad.chi.core.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.Unit;

import java.util.List;

import static gh.marad.chi.core.Type.typeParameter;

public class ArrayClearBuiltin extends CollectionsArrayBuiltin {
    @Override
    public FnType type() {
        var T = typeParameter("T");
        return Type.genericFn(
                List.of(T),      // generic type parameters
                Type.getUnit(),  // return type
                Type.array(T));  // array
    }

    @Override
    public String name() {
        return "clear";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.ArrayClearBuiltin;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var array = ChiArgs.getChiArray(frame, 0);
        array.clear();
        return Unit.instance;
    }
}
