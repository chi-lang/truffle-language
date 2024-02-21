package gh.marad.chi.language.builtin.io;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.ChiArray;

public class ArgsBuiltin extends Builtin {

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var args = ChiContext.get(this).getEnv().getApplicationArguments();
        var truffleStrings = new TruffleString[args.length];
        for (var i = 0; i < args.length; i++) {
            truffleStrings[i] = TruffleString.fromJavaStringUncached(args[i], TruffleString.Encoding.UTF_8);
        }
        return new ChiArray(truffleStrings, Type.getString());
    }

    @Override
    public Function type() {
        return Type.fn(Type.array(Type.getString()));
    }

    @Override
    public String getModuleName() {
        return "std";
    }

    @Override
    public String getPackageName() {
        return "io";
    }

    @Override
    public String name() {
        return "programArguments";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.ArgsBuiltin;
    }
}
