package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.FnType;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.image.NodeId;

import java.util.Arrays;
import java.util.List;

import static gh.marad.chi.core.Type.*;

public class ArrayHashBuiltin extends CollectionsArrayBuiltin {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var array = ChiArgs.getChiArray(frame, 0);
        return Arrays.hashCode(array.unsafeGetUnderlyingArray());
    }

    @Override
    public FnType type() {
        return genericFn(
                List.of(typeParameter("T")),
                getIntType(),
                array(typeParameter("T")));
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
