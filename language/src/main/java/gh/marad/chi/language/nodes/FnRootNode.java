package gh.marad.chi.language.nodes;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import gh.marad.chi.language.nodes.expr.flow.ReturnException;

public class FnRootNode extends RootNode {
    @Child
    private ChiNode body;
    private final String name;

    private final SourceSection sourceSection;

    public FnRootNode(TruffleLanguage<?> language, FrameDescriptor frameDescriptor, ChiNode body, String name) {
        super(language, frameDescriptor);
        this.body = body;
        this.name = name;
        body.addRootTag();
        var source = Source.newBuilder("chi", "foo", "dummy.chi").build();
        this.sourceSection = source.createSection(1);
    }

    @Override
    public String getName() {
        var parent = getParent();
        if (parent != null) {
            var parentRoot = parent.getRootNode();
            return "%s-%s".formatted(parentRoot.getName(), name);
        }
        return name;
    }


    @Override
    public SourceSection getSourceSection() {
        return sourceSection;
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        try {
            return body.executeGeneric(frame);
        } catch (ReturnException e) {
            return e.value;
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    public void accept(ChiNodeVisitor visitor) throws Exception {
        body.accept(visitor);
    }

}
