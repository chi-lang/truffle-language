package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.FnType;
import gh.marad.chi.core.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.Unit;

import java.util.Arrays;
import java.util.List;

import static gh.marad.chi.core.Type.*;

public class ArraySortBuiltin extends CollectionsArrayBuiltin {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var array = ChiArgs.getChiArray(frame, 0);
        Arrays.sort(array.unsafeGetUnderlyingArray());
        return Unit.instance;
    }

    @Override
    public FnType type() {
        return genericFn(
                List.of(typeParameter("T")),
                Type.getUnit(),
                array(typeParameter("T")));
    }

    @Override
    public String name() {
        return "sort";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.ArraySortBuiltin;
    }
}
