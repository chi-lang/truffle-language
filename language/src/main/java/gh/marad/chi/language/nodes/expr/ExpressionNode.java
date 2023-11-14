package gh.marad.chi.language.nodes.expr;

import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import gh.marad.chi.language.nodes.ScopedNode;

public abstract class ExpressionNode extends ScopedNode {


    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == StandardTags.ExpressionTag.class || super.hasTag(tag);
    }
}
