package gh.marad.chi.language.builtin.collections;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.FnType;
import gh.marad.chi.core.Type;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.ChiArray;

import java.util.List;

import static gh.marad.chi.core.Type.*;

public class EmptyArrayBuiltin extends CollectionsArrayBuiltin {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return new ChiArray(Type.getUndefined());
    }

    @Override
    public FnType type() {
        return genericFn(
                List.of(typeParameter("T")),
                array(typeParameter("T")));
    }

    @Override
    public String name() {
        return "emptyArray";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.EmptyArrayBuiltin;
    }
}
