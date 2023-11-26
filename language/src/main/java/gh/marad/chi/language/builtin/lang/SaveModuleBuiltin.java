package gh.marad.chi.language.builtin.lang;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.FnType;
import gh.marad.chi.core.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.ImageWritingVisitor;
import gh.marad.chi.language.image.TypeWriter;
import gh.marad.chi.language.nodes.FnRootNode;
import gh.marad.chi.language.runtime.StdStreams;
import gh.marad.chi.language.runtime.TODO;
import gh.marad.chi.language.runtime.Unit;
import gh.marad.chi.language.runtime.namespaces.Module;
import gh.marad.chi.language.runtime.namespaces.Package;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class SaveModuleBuiltin extends Builtin {
    @Override
    public FnType type() {
        return Type.fn(Type.getUnit(), Type.getString(), Type.getString());
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
        return "saveModule";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var context = ChiContext.get(this);
        var moduleName = ChiArgs.getTruffleString(frame, 0).toString();
        var fileName = ChiArgs.getTruffleString(frame, 1).toString();
        var module = context.modules.getOrCreateModule(moduleName);
        save(module, fileName);
        return Unit.instance;
    }

    @CompilerDirectives.TruffleBoundary
    public void save(Module module, String fileName) {
        var chiContext = ChiContext.get(this);
        var std = new StdStreams(chiContext);
        try {
            var output = new DataOutputStream(new FileOutputStream(fileName));
            writeModule(module, output, std);
            output.close();
        } catch (Throwable e) {
            std.err.println(e.getMessage());
            std.err.flush();
        }
    }

    public void writeModule(Module module, DataOutputStream stream, StdStreams std) throws Exception {
        std.out.println("Module " + module.getName());
        std.out.flush();
        var packageNames = module.listPackages();
        stream.writeUTF(module.getName());  // module name
        stream.writeShort(packageNames.size()); // package count
        for (String packageName : module.listPackages()) {
            writePackage(module, packageName, stream, std); // package array
        }
    }

    public void writePackage(Module module, String packageName, DataOutputStream stream, StdStreams std) throws Exception {
        std.out.println("|- Package " + packageName);
        std.out.flush();
        var functions = module.listFunctions(packageName);
        stream.writeUTF(packageName);     // package name
        stream.writeShort(functions.size());  // function count

        var imageWritingVisitor = new ImageWritingVisitor(stream);
        for (Package.FunctionLookupResult function : functions) {
            var rootNode = function.function().getCallTarget().getRootNode();
            if (rootNode instanceof FnRootNode) {
                FnRootNode node = (FnRootNode) rootNode;
                std.out.println("|  |- Function " + node.getName());
                std.out.flush();
                stream.writeUTF(node.getName());
                TypeWriter.writeType(function.type(), stream);
                stream.writeBoolean(function.isPublic());
                node.accept(imageWritingVisitor);
            } else {
                throw new TODO("Non FnRootNode as function body!");
            }
        }
    }
}
