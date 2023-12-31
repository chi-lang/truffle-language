package gh.marad.chi.language.builtin.lang;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import gh.marad.chi.core.FnType;
import gh.marad.chi.core.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiLanguage;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;

public class EvalBuiltin extends Builtin {
    @Child
    private IndirectCallNode indirectCallNode;

    public EvalBuiltin() {
        this.indirectCallNode = Truffle.getRuntime().createIndirectCallNode();
    }

    @Override
    public FnType type() {
        return Type.fn(Type.getUnit(), Type.getString());
    }

    @Override
    public String getModuleName() {
        return "std";
    }

    @Override
    public String getPackageName() {
        return "lang";
    }

    @Override
    public String name() {
        return "eval";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var string = ChiArgs.getTruffleString(frame, 0);
        var callTarget = ChiLanguage.get(this).compile(string.toJavaStringUncached());
        return indirectCallNode.call(callTarget);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.EvalBuiltin;
    }
}
