package gh.marad.chi.language.image;

import com.oracle.truffle.api.CompilerDirectives;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.nodes.FnRootNode;
import gh.marad.chi.language.runtime.StdStreams;
import gh.marad.chi.language.runtime.TODO;
import gh.marad.chi.language.runtime.namespaces.Module;
import gh.marad.chi.language.runtime.namespaces.Package;

import java.io.DataOutputStream;

public class ModuleWriter {
    private final ChiContext context;

    public ModuleWriter(ChiContext context) {
        this.context = context;
    }

    @CompilerDirectives.TruffleBoundary
    public void save(Module module, DataOutputStream output) {
        var std = new StdStreams(context);
        try {
            writeModule(module, output, std);
        } catch (Throwable e) {
            std.err.println(e.getMessage());
            std.err.flush();
        }
    }

    private void writeModule(Module module, DataOutputStream stream, StdStreams std) throws Exception {
        std.out.println("Module " + module.getName());
        std.out.flush();
        var packageNames = module.listPackages();
        stream.writeUTF(module.getName());  // module name
        stream.writeShort(packageNames.size()); // package count
        for (String packageName : module.listPackages()) {
            writePackage(module, packageName, stream, std); // package array
        }
    }

    private void writePackage(Module module, String packageName, DataOutputStream stream, StdStreams std) throws Exception {
        std.out.println("|- Package " + packageName);
        std.out.flush();
        var functions = module.listFunctions(packageName);
        stream.writeUTF(packageName);     // package name
        stream.writeShort(functions.size());  // function count

        var imageWritingVisitor = new ImageWritingVisitor(stream);
        for (Package.FunctionLookupResult function : functions) {
            var rootNode = function.function().getCallTarget().getRootNode();
            if (rootNode instanceof FnRootNode node) {
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
