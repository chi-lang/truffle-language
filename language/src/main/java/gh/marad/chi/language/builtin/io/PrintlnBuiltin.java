package gh.marad.chi.language.builtin.io;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiTypesGen;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.Unit;

import java.io.OutputStream;
import java.io.PrintWriter;

public class PrintlnBuiltin extends Builtin {
    private final PrintWriter writer;


    @CompilerDirectives.TruffleBoundary
    public PrintlnBuiltin(OutputStream stream) {
        writer = new PrintWriter(stream);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var message = ChiArgs.getObject(frame, 0);
        printMessage(message);
        return Unit.instance;
    }

    @CompilerDirectives.TruffleBoundary
    private void printMessage(Object message) {
        writer.println(ChiTypesGen.asImplicitTruffleString(message));
        writer.flush();
    }

    @Override
    public Function type() {
        return Type.fn(Type.getAny(), Type.getUnit());
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
        return "println";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.PrintlnBuiltin;
    }
}
