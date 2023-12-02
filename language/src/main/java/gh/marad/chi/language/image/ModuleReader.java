package gh.marad.chi.language.image;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import gh.marad.chi.core.FnType;
import gh.marad.chi.core.namespace.SymbolType;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.ChiLanguage;
import gh.marad.chi.language.nodes.FnRootNode;
import gh.marad.chi.language.runtime.ChiFunction;
import gh.marad.chi.language.runtime.StdStreams;
import gh.marad.chi.language.runtime.namespaces.Module;

import java.io.DataInputStream;
import java.io.IOException;

public class ModuleReader {
    private final ChiLanguage language;
    private final ChiContext context;

    public ModuleReader(ChiLanguage language, ChiContext context) {
        this.language = language;
        this.context = context;
    }

    @CompilerDirectives.TruffleBoundary
    public void readModule(DataInputStream stream, StdStreams std) throws IOException {
        var moduleName = stream.readUTF();
        var packageCount = stream.readShort();
        var module = context.modules.getOrCreateModule(moduleName);
        for (int i = 0; i < packageCount; i++) {
            readPackage(module, stream, std);
        }
    }

    private void readPackage(Module module, DataInputStream stream, StdStreams std) throws IOException {
        var packageName = stream.readUTF();
        var functionCount = stream.readShort();

        var nodeReader = new NodeReader(stream, context.getEnv().out());
        for (int i = 0; i < functionCount; i++) {
            var functionName = stream.readUTF();
            var type = (FnType) TypeWriter.readType(stream);
            var isPublic = stream.readBoolean();
            var functionBody = nodeReader.readNode();

            try {
                var frameDescriptorBuilder = FrameDescriptor.newBuilder();
                var localCounter = new LocalVarsCountingVisitor();
                frameDescriptorBuilder.addSlots(localCounter.getCount(), FrameSlotKind.Illegal);
                functionBody.accept(localCounter);
                var rootNode = new FnRootNode(
                        language,
                        frameDescriptorBuilder.build(),
                        functionBody,
                        functionName
                );
                var function = new ChiFunction(rootNode.getCallTarget());
                module.defineFunction(packageName, function, type, isPublic);
                context.compilationNamespace
                        .getOrCreatePackage(module.getName(), packageName)
                        .getScope().addSymbol(
                               functionName,
                               type, SymbolType.Local,
                               isPublic, true
                       );
            } catch (Exception ex) {
                std.err.printf("Error loading function %s in %s/%s%n", functionName, module.getName(), packageName);
                ex.printStackTrace(std.err);
                std.err.flush();
            }
        }
    }

}
