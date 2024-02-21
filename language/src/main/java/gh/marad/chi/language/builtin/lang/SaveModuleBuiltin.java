package gh.marad.chi.language.builtin.lang;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.ModuleWriter;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.TODO;
import gh.marad.chi.language.runtime.Unit;
import gh.marad.chi.language.runtime.namespaces.Module;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveModuleBuiltin extends Builtin {
    @Override
    public Function type() {
        return Type.fn(Type.getString(), Type.getString(), Type.getUnit());
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
        save(context, module, fileName);
        return Unit.instance;
    }

    @CompilerDirectives.TruffleBoundary
    private void save(ChiContext context, Module module, String fileName) {
        try(var output = new DataOutputStream(new FileOutputStream(fileName))) {
            var writer = new ModuleWriter(context);
            writer.save(module, output);
        } catch (FileNotFoundException e) {
            throw new TODO("File %s not found".formatted(fileName));
        } catch (IOException e) {
            throw new TODO(e);
        }
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.SaveModuleBuiltin;
    }
}
