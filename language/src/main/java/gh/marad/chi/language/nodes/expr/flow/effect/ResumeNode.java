package gh.marad.chi.language.nodes.expr.flow.effect;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiLanguage;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.FnRootNode;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.ChiFunction;

public class ResumeNode extends ExpressionNode {

    public ResumeNode() {
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var value = ChiArgs.getObject(frame, 0);
        throw new ResumeValueException(value);
    }

    public static ChiFunction createResumeFunction() {
        var resumeNode = new ResumeNode();
        var language = ChiLanguage.get(resumeNode);
        var rootNode = new FnRootNode(language, FrameDescriptor.newBuilder().build(), resumeNode, "resume");
        return new ChiFunction(rootNode.getCallTarget());
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitResumeNode(this);
    }
}
