package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Variable;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.Unit;

import java.util.List;


public class ArrayAddAtBuiltin extends CollectionsArrayBuiltin {
    @Override
    public Function type() {
        var T = new Variable("T", 0);
        return new Function(
                List.of(
                        Type.array(T), Type.getInt(), T, Type.getUnit()
                ),
                List.of("T")
        );
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
