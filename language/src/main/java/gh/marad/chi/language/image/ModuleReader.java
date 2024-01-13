package gh.marad.chi.language.image;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.ChiLanguage;
import gh.marad.chi.language.nodes.FnRootNode;
import gh.marad.chi.language.runtime.ChiFunction;
import gh.marad.chi.language.runtime.StdStreams;
import gh.marad.chi.language.runtime.TODO;
import gh.marad.chi.language.runtime.namespaces.Module;

import java.io.DataInputStream;

public class ModuleReader {
    private final ChiLanguage language;
    private final ChiContext context;

    public ModuleReader(ChiLanguage language, ChiContext context) {
        this.language = language;
        this.context = context;
    }

    @CompilerDirectives.TruffleBoundary
    public void readModule(DataInputStream stream, StdStreams std, TruffleLanguage.Env env) throws Exception {
        var moduleName = stream.readUTF();
        var packageCount = stream.readShort();
        var module = context.modules.getOrCreateModule(moduleName);
        for (int i = 0; i < packageCount; i++) {
            readPackage(module, stream, std, env);
        }
    }

    private void readPackage(Module module, DataInputStream stream, StdStreams std, TruffleLanguage.Env env) throws Exception {
        var packageName = stream.readUTF();
        var nodeReader = new NodeReader(stream, context.getEnv().out());

        // read types
        int typeCount = stream.readShort();
        for (int i = 0; i < typeCount; i++) {
            var typeInfo = TypeWriter.readTypeInfo(stream);
            module.defineType(packageName, typeInfo);
        }

        // read package functions
        var functionCount = stream.readShort();
        for (int i = 0; i < functionCount; i++) {
            var functionName = stream.readUTF();
            var type = (FunctionType) TypeWriter.readType(stream);
            var isPublic = stream.readBoolean();
            var functionBody = nodeReader.readNode();

            try {
                var frameDescriptorBuilder = FrameDescriptor.newBuilder();
                var localCounter = new LocalVarsCountingVisitor();
                functionBody.accept(localCounter);
                frameDescriptorBuilder.addSlots(localCounter.getCount(), FrameSlotKind.Illegal);
                var rootNode = new FnRootNode(
                        language,
                        frameDescriptorBuilder.build(),
                        functionBody,
                        functionName
                );
                var function = new ChiFunction(rootNode.getCallTarget());
                module.defineFunction(packageName, function, type, isPublic);
            } catch (Exception ex) {
                std.err.printf("Error loading function %s in %s/%s%n", functionName, module.getName(), packageName);
                ex.printStackTrace(std.err);
                std.err.flush();
            }
        }

        // read package variables
        var variableCount = stream.readShort();
        for (int i = 0; i < variableCount; i++) {
            var name = stream.readUTF();
            var value = ValueWriter.readValue(stream, env);
            var type = TypeWriter.readType(stream);
            var isPublic = stream.readBoolean();
            var isMutable = stream.readBoolean();
            module.defineVariable(packageName, name, value, type, isPublic, isMutable);
        }

    }

}
