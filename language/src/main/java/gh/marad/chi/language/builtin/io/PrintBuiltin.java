package gh.marad.chi.language.builtin.io;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.FnType;
import gh.marad.chi.core.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiTypesGen;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.Unit;

import java.io.OutputStream;
import java.io.PrintWriter;

public class PrintBuiltin extends Builtin {
    private final PrintWriter writer;

    @CompilerDirectives.TruffleBoundary
    public PrintBuiltin(OutputStream stream) {
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
        writer.print(ChiTypesGen.asImplicitTruffleString(message));
        writer.flush();
    }

    @Override
    public FnType type() {
        return Type.Companion.fn(Type.Companion.getUnit(), Type.Companion.getAny());
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
        return "print";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.PrintBuiltin;
    }
}
