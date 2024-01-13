package gh.marad.chi.language.nodes.objects;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.Unit;

import java.util.List;

public class DefineVariantTypeNode extends ExpressionNode {
//    public final VariantType type;
//    public final List<VariantType.Variant> variants;
//
//    public DefineVariantTypeNode(VariantType type, List<VariantType.Variant> variants) {
//        this.type = type;
//        this.variants = variants;
//    }


    @Override
    public Object executeGeneric(VirtualFrame frame) {
//        defineType();
        return Unit.instance;
    }

//    @CompilerDirectives.TruffleBoundary
//    private void defineType() {
//        var context = ChiContext.get(this);
//        context.modules.getOrCreateModule(type.getModuleName())
//                       .defineVariantType(type.getPackageName(), type, variants);
//    }


    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitDefineVariantTypeNode(this);
    }
}
