package gh.marad.chi.language.nodes.expr.flow;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.ConditionProfile;
import com.oracle.truffle.api.profiles.CountingConditionProfile;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.nodes.expr.operators.bool.LogicNotOperator;
import gh.marad.chi.language.runtime.TODO;
import gh.marad.chi.language.runtime.Unit;

public class IfExpr extends ExpressionNode {

    private @Child ChiNode condition;
    private @Child ChiNode thenBranch;
    private @Child ChiNode elseBranch;

    public ChiNode getCondition() {
        return condition;
    }

    public ChiNode getThenBranch() {
        return thenBranch;
    }

    public ChiNode getElseBranch() {
        return elseBranch;
    }

    private final CountingConditionProfile exprProfile = CountingConditionProfile.create();

    public static IfExpr create(ChiNode condition, ChiNode thenBranch, ChiNode elseBranch) {
        if (elseBranch != null && condition instanceof LogicNotOperator not) {
            // if (!cond) a else b  => if (cond) b else a
            return new IfExpr(not.getValue(), elseBranch, thenBranch);
        } else {
            return new IfExpr(condition, thenBranch, elseBranch);
        }
    }

    private IfExpr(ChiNode condition, ChiNode thenBranch, ChiNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            var cond = condition.executeBoolean(frame);
            if (exprProfile.profile(cond)) {
                if (thenBranch != null) {
                    return thenBranch.executeGeneric(frame);
                } else {
                    return Unit.instance;
                }
            } else {
                if (elseBranch != null) {
                    return elseBranch.executeGeneric(frame);
                } else {
                    return Unit.instance;
                }
            }
        } catch (UnexpectedResultException ex) {
            throw new TODO(ex);
        }
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitIfExpr(this);
        condition.accept(visitor);
        thenBranch.accept(visitor);
        elseBranch.accept(visitor);
    }
}
