package gh.marad.chi.language.builtin.io;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.Types;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReadStringBuiltin extends Builtin {

    private final TruffleString.FromJavaStringNode node = TruffleString.FromJavaStringNode.create();

    @Override
    public TruffleString executeString(VirtualFrame frame) {
        var filePath = ChiArgs.getTruffleString(frame, 0);
        return readString(filePath);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeString(frame);
    }

    @CompilerDirectives.TruffleBoundary
    private TruffleString readString(TruffleString path) {
        try {
            var javaString = Files.readString(Path.of(path.toJavaStringUncached()));
            return node.execute(javaString, TruffleString.Encoding.UTF_8);
        } catch (IOException e) {
            CompilerDirectives.transferToInterpreter();
            throw new RuntimeException(e);
        }
    }

    @Override
    public FunctionType type() {
        return Types.fn(Types.getString(), Types.getString());
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
        return "readString";
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.ReadStringBuiltin;
    }
}
