package gh.marad.chi.language.nodes.expr.flow.loop;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.BranchProfile;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.TODO;

public class WhileRepeatingNode extends ExpressionNode implements RepeatingNode {

    @Child
    private ChiNode conditionNode;
    @Child
    private ChiNode bodyNode;

    private final BranchProfile continueTaken = BranchProfile.create();
    private final BranchProfile breakTaken = BranchProfile.create();

    public WhileRepeatingNode(ChiNode conditionNode, ChiNode bodyNode) {
        this.conditionNode = conditionNode;
        this.bodyNode = bodyNode;
    }

    @Override
    public boolean executeRepeating(VirtualFrame frame) {
        try {
            if (!conditionNode.executeBoolean(frame)) {
                return false;
            }
            bodyNode.executeVoid(frame);
            return true;
        } catch (UnexpectedResultException ex) {
            throw new TODO(ex);
        } catch (WhileBreakException ex) {
            breakTaken.enter();
            return false;
        } catch (WhileContinueException ex) {
            continueTaken.enter();
            return true;
        }
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        throw new TODO("while is not a regular expression node");
    }

    public ChiNode getConditionNode() {
        return conditionNode;
    }

    public ChiNode getBodyNode() {
        return bodyNode;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        throw new TODO("Visitor should not reach here!");
    }

    //    @Override
//    public Object executeRepeatingWithValue(VirtualFrame frame) {
//        return RepeatingNode.super.executeRepeatingWithValue(frame);
//    }
//
//    @Override
//    public Object initialLoopStatus() {
//        return RepeatingNode.super.initialLoopStatus();
//    }
//
//    @Override
//    public boolean shouldContinue(Object returnValue) {
//        return RepeatingNode.super.shouldContinue(returnValue);
//    }
}
