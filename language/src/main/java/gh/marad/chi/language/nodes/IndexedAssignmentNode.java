package gh.marad.chi.language.nodes;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.ChiArray;
import gh.marad.chi.language.runtime.TODO;

@NodeChild(value = "variable", type = ChiNode.class)
@NodeChild(value = "index", type = ChiNode.class)
@NodeChild(value = "value", type = ChiNode.class)
public abstract class IndexedAssignmentNode extends ExpressionNode {

    public abstract ChiNode getVariable();
    public abstract ChiNode getIndex();
    public abstract ChiNode getValue();

    @Specialization
    public Object doChiArray(ChiArray array, long index, Object value) {
        try {
            array.writeArrayElement(index, value);
            return value;
        } catch (InvalidArrayIndexException ex) {
            throw new TODO("Implement runtime error handling!", ex);
        }
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitIndexedAssignmentNode(this);
        getVariable().accept(visitor);
        getIndex().accept(visitor);
        getValue().accept(visitor);
    }
}
