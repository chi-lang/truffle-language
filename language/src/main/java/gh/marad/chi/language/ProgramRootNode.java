package gh.marad.chi.language;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.runtime.Unit;

import java.io.PrintStream;

public class ProgramRootNode extends RootNode {
    private final PrintStream stderr;
    @Child
    private ChiNode body;
    private final FrameDescriptor frameDescriptor;

    protected ProgramRootNode(TruffleLanguage<?> language, ChiContext context, ChiNode body, FrameDescriptor frameDescriptor) {
        super(language);
        this.stderr = new PrintStream(context.getEnv().err());
        var source = Source.newBuilder("chi", "foo", "dummy.chi").build();
        this.body = body;
        this.frameDescriptor = frameDescriptor;
    }

    @Override
    public String getName() {
        return "[root]";
    }

    @Override
    public Object execute(VirtualFrame frame) {
        var globalScope = ChiContext.get(this).globalScope;
        var mainFrame = Truffle.getRuntime().createVirtualFrame(
                ChiArgs.create(globalScope), frameDescriptor);
        try {
            return body.executeGeneric(mainFrame);
        } catch (Exception ex) {
            CompilerDirectives.transferToInterpreter();
            var truffleStackTrace = TruffleStackTrace.getStackTrace(ex);
            stderr.printf("Error: %s%n", ex.getMessage());
            for (TruffleStackTraceElement element : truffleStackTrace) {
                var functionName = element.getTarget().getRootNode().getName();
                var source = element.getTarget().getRootNode().getSourceSection();
                String location = "";

                if (source != null) {
                    var fileName = source.getSource().getName();
                    var line = source.getStartLine();
                    var col = source.getStartColumn();
                    location = " [%s %d:%d]".formatted(fileName, line, col);
                }

                stderr.printf(
                        " - %s%s%n",
                        functionName,
                        location
                );
            }
            stderr.flush();

            Throwable cause = ex.getCause();
            while (cause != null) {
                if (cause.getMessage() != null) {
                    stderr.println("Cause: " + cause.getMessage());
                }
                cause = cause.getCause();
            }

            return Unit.instance;
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
