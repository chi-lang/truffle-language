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

    // TODO implement saving/loading package variables
    // TODO implement saving/loading custom data types

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
        var packageNames = module.listPackages();
        stream.writeUTF(module.getName());  // module name
        stream.writeShort(packageNames.size()); // package count
        for (String packageName : module.listPackages()) {
            writePackage(module, packageName, stream, std); // package array
        }
    }

    private void writePackage(Module module, String packageName, DataOutputStream stream, StdStreams std) throws Exception {
        var functions = module.listFunctions(packageName);
        stream.writeUTF(packageName);     // package name
        stream.writeShort(functions.size());  // function count

        var imageWritingVisitor = new ImageWritingVisitor(stream);
        for (Package.FunctionLookupResult function : functions) {
            try {
                var rootNode = function.function().getCallTarget().getRootNode();
                if (rootNode instanceof FnRootNode node) {
                    stream.writeUTF(node.getName());
                    TypeWriter.writeType(function.type(), stream);
                    stream.writeBoolean(function.isPublic());
                    node.accept(imageWritingVisitor);
                } else {
                    throw new TODO("Non FnRootNode as function body!");
                }
            } catch (Exception ex) {
                std.err.printf("Error saving function %s in %s/%s%n", function.function().getExecutableName(), module.getName(), packageName);
                ex.printStackTrace(std.err);
                std.err.flush();
            }
        }
    }
}
