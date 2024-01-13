package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.TypeVariable;
import gh.marad.chi.core.types.Types;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.Unit;

import java.util.List;


public class ArrayRemoveAtBuiltin extends CollectionsArrayBuiltin {
    @Override
    public FunctionType type() {
        var T = new TypeVariable("T");
        return new FunctionType(
                List.of(Types.array(T), Types.getInt(), Types.getUnit()),
                List.of(T)
        );
    }

    @Override
    public String name() {
        return "removeAt";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.ArrayRemoveAtBuiltin;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var array = ChiArgs.getChiArray(frame, 0);
        var index = ChiArgs.getLong(frame,1);
        array.removeAt((int) index);
        return Unit.instance;
    }
}
