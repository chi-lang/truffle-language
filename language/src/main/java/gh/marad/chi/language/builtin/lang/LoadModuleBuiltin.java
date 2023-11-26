package gh.marad.chi.language.builtin.lang;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.FnType;
import gh.marad.chi.core.Type;
import gh.marad.chi.core.namespace.SymbolType;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.ChiLanguage;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.image.TypeWriter;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.FnRootNode;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.variables.ReadLocalArgument;
import gh.marad.chi.language.nodes.function.GetDefinedFunction;
import gh.marad.chi.language.nodes.function.InvokeFunction;
import gh.marad.chi.language.nodes.value.StringValue;
import gh.marad.chi.language.runtime.ChiFunction;
import gh.marad.chi.language.runtime.StdStreams;
import gh.marad.chi.language.runtime.Unit;
import gh.marad.chi.language.runtime.namespaces.Module;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class LoadModuleBuiltin extends Builtin {
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
        return "lang.image";
    }

    @Override
    public String name() {
        return "loadModule";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var fileName = ChiArgs.getTruffleString(frame, 0);
        load(fileName.toString());
        return Unit.instance;
    }

    @CompilerDirectives.TruffleBoundary
    void load(String fileName) {
        var context = ChiContext.get(this);
        var std = new StdStreams(context);
        try {
            var stream = new DataInputStream(new FileInputStream(fileName));
            loadModule(context, stream, std);
            stream.close();
        } catch (Throwable e) {
            std.err.println(e.getMessage());
            std.err.flush();
        }
    }

    void loadModule(ChiContext context, DataInputStream stream, StdStreams std) throws IOException {
        var moduleName = stream.readUTF();
        var packageCount = stream.readShort();
        std.out.println("Module " + moduleName);
        std.out.flush();
        var module = context.modules.getOrCreateModule(moduleName);
        for (int i = 0; i < packageCount; i++) {
            loadPackage(module, stream, std);
        }
    }

    private void loadPackage(Module module, DataInputStream stream, StdStreams std) throws IOException {
        var packageName = stream.readUTF();
        var functionCount = stream.readShort();
        std.out.println("|- Package " + packageName);
        std.out.flush();

        var context = ChiContext.get(this);
        for (int i = 0; i < functionCount; i++) {
            var functionName = stream.readUTF();
            var type = (FnType) TypeWriter.readType(stream);
            var isPublic = stream.readBoolean();
            var functionBody = readNode(stream);

            std.out.println("|  |- Function " + functionName);
            std.out.flush();

            var frameDescriptor = FrameDescriptor.newBuilder().build();
            var rootNode = new FnRootNode(
                    ChiLanguage.get(this),
                    frameDescriptor,
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
        }
    }

    private ChiNode readNode(DataInputStream stream) throws IOException {
        var nodeId = NodeId.fromId(stream.readShort());

        return switch (nodeId) {
            case Block -> readBlock(stream);
            case ReadLocalArgument -> readLocalArgument(stream);
            case InvokeFunction -> readInvokeFunction(stream);
            case StringValue -> readStringValue(stream);
            case GetDefinedFunction -> readGetDefinedFunction(stream);
        };
    }

    private ChiNode readBlock(DataInputStream stream) throws IOException {
        var bodySize = stream.readShort();
        var elements = new ChiNode[bodySize];
        for (int i = 0; i < bodySize; i++) {
            elements[i] = readNode(stream);
        }
        return new BlockExpr(elements);
    }

    private ChiNode readLocalArgument(DataInputStream stream) throws IOException {
        var slot = stream.readByte();
        return new ReadLocalArgument(slot);
    }

    private ChiNode readInvokeFunction(DataInputStream stream) throws IOException {
        var argCount = stream.readByte();
        var arguments = new ChiNode[argCount];
        for (byte i = 0; i < argCount; i++) {
            arguments[i] = readNode(stream);
        }
        var function = readNode(stream);
        return new InvokeFunction(function, arguments);
    }

    private ChiNode readStringValue(DataInputStream stream) throws IOException {
        return new StringValue(stream.readUTF());
    }

    private ChiNode readGetDefinedFunction(DataInputStream stream) throws IOException {
        var moduleName = stream.readUTF();
        var packageName = stream.readUTF();
        var functionName = stream.readUTF();
        var paramTypes = TypeWriter.readTypes(stream);
        return new GetDefinedFunction(moduleName, packageName, functionName, paramTypes);
    }

}
