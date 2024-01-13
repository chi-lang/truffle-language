package gh.marad.chi.language.builtin.lang;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.Types;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.Unit;

public class ClearPackageBuiltin extends Builtin {
    @Override
    public FunctionType type() {
        return Types.fn(Types.getString(), Types.getString(), Types.getUnit());
    }

    @Override
    public String getModuleName() {
        return "std";
    }

    @Override
    public String getPackageName() {
        return "lang";
    }

    @Override
    public String name() {
        return "clearPackage";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var moduleName = ChiArgs.getTruffleString(frame, 0);
        var packageName = ChiArgs.getTruffleString(frame, 1);
        clearPackage(
                moduleName.toJavaStringUncached(),
                packageName.toJavaStringUncached());
        return Unit.instance;
    }

    private void clearPackage(String moduleName, String packageName) {
        var ctx = ChiContext.get(this);
        ctx.modules.getOrCreateModule(moduleName).removePackage(packageName);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.ClearPackageBuiltin;
    }

}
