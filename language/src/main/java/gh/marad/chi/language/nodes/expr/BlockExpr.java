package gh.marad.chi.language.nodes.expr;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.BlockNode;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;

public class BlockExpr extends ExpressionNode implements BlockNode.ElementExecutor<ChiNode> {
    @Child
    private BlockNode<ChiNode> block;

    public BlockExpr(ChiNode[] elements) {
        this.block = BlockNode.create(elements, this);
    }

    public ChiNode[] getElements() {
        return block.getElements();
    }

    @Override
    public void executeVoid(VirtualFrame frame, ChiNode node, int index, int argument) {
        node.executeVoid(frame);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame, ChiNode node, int index, int argument) {
        return node.executeGeneric(frame);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return block.executeGeneric(frame, 0);
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitBlockExpr(this);
        for (ChiNode node : block.getElements()) {
            node.accept(visitor);
        }
    }
}
