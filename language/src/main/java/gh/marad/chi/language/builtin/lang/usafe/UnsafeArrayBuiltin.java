package gh.marad.chi.language.builtin.lang.usafe;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.FnType;
import gh.marad.chi.core.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.ChiArray;

import java.util.List;

import static gh.marad.chi.core.Type.*;

public class UnsafeArrayBuiltin extends LangUnsafeBuiltin {
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var capacity = ChiArgs.getLong(frame, 0);
        return new ChiArray((int) capacity, Type.getUndefined());
    }

    @Override
    public FnType type() {
        return genericFn(
                List.of(typeParameter("T")),
                array(typeParameter("T")),
                getIntType());
    }

    @Override
    public String name() {
        return "array";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.UnsafeArrayBuiltin;
    }
}
