package gh.marad.chi.language.builtin.lang.interop;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.ChiHostSymbol;

public class LookupHostSymbolBuiltin extends LangInteropBuiltin {
    @Child
    private TruffleString.ToJavaStringNode toJavaString;

    public LookupHostSymbolBuiltin() {
        this.toJavaString = TruffleString.ToJavaStringNode.create();
    }

    @Override
    public Function type() {
        return Type.fn(Type.getString(), Type.getAny());
    }

    @Override
    public String name() {
        return "lookupHostSymbol";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var hostSymbolName = ChiArgs.getTruffleString(frame, 0);
        var env = ChiContext.get(this).getEnv();
        var symbolName = toJavaString.execute(hostSymbolName);
        var symbol = env.lookupHostSymbol(symbolName);
        return new ChiHostSymbol(symbolName, symbol);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.LookupHostSymbolBuiltin;
    }
}
