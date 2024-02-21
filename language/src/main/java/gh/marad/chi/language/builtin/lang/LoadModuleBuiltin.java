package gh.marad.chi.language.builtin.lang;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.Function;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.ChiLanguage;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.ModuleReader;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.StdStreams;
import gh.marad.chi.language.runtime.Unit;

import java.io.DataInputStream;
import java.io.FileInputStream;

public class LoadModuleBuiltin extends Builtin {
    @Override
    public Function type() {
        return Type.fn(Type.getString(), Type.getUnit());
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
        var language = ChiLanguage.get(this);
        var context = ChiContext.get(this);
        var std = new StdStreams(context);
        try {
            var stream = new DataInputStream(new FileInputStream(fileName));
            var moduleReader = new ModuleReader(language, context);
            moduleReader.readModule(stream, std, context.getEnv());
            stream.close();
        } catch (Throwable e) {
            e.printStackTrace(std.err);
            std.err.flush();
        }
    }


    @Override
    public NodeId getNodeId() {
        return NodeId.LoadModuleBuiltin;
    }

}
