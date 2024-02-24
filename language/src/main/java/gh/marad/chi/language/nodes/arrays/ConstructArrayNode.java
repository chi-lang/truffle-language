package gh.marad.chi.language.nodes.arrays;

import com.oracle.truffle.api.frame.VirtualFrame;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.runtime.ChiArray;
import gh.marad.chi.language.runtime.TODO;

import java.util.ArrayList;

public class ConstructArrayNode extends ChiNode {
    private final ChiNode[] values;
    private final Type elementType;

    public ConstructArrayNode(ChiNode[] values, Type elementType) {
        this.values = values;
        this.elementType = elementType;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var data = new ArrayList<Object>();
        for (ChiNode valueNode : values) {
            data.add(valueNode.executeGeneric(frame));
        }
        return new ChiArray(data, elementType);
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        throw new TODO("Not implemented yet");
    }
}
