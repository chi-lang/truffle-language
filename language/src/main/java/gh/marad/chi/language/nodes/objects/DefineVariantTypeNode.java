package gh.marad.chi.language.nodes.objects;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.VariantType;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.Unit;

import java.util.List;

public class DefineVariantTypeNode extends ExpressionNode {
    public final VariantType type;
    public final List<VariantType.Variant> variants;

    public DefineVariantTypeNode(VariantType type, List<VariantType.Variant> variants) {
        this.type = type;
        this.variants = variants;
    }


    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var context = ChiContext.get(this);
        context.modules.getOrCreateModule(type.getModuleName())
                .defineVariantType(type.getPackageName(), type, variants);
        return Unit.instance;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitDefineVariantTypeNode(this);
    }
}
