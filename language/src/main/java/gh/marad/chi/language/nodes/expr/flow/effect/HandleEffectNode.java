package gh.marad.chi.language.nodes.expr.flow.effect;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.EffectHandlers;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.ChiFunction;

import java.util.Map;

public class HandleEffectNode extends ExpressionNode {

    @Child
    private BlockExpr block;
    public final Map<EffectHandlers.Qualifier, ChiFunction> handlers;

    public HandleEffectNode(BlockExpr resumableBlockNode, Map<EffectHandlers.Qualifier, ChiFunction> handlers) {
        this.block = resumableBlockNode;
        this.handlers = handlers;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            var context = ChiContext.get(this);
            context.pushHandlers(handlers);
            var result = block.executeGeneric(frame);
            context.popHandlers();
            return result;
        } catch (AbortEffectWithValueException ex) {
            return ex.getValue();
        }
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitHandleEffect(this);
        block.accept(visitor);
    }
}
