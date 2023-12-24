package gh.marad.chi.language.nodes.value;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.language.nodes.ChiNodeVisitor;

import java.nio.charset.StandardCharsets;

public class StringValue extends ValueNode {
    public final TruffleString value;

    public StringValue(String value) {
        this.value = TruffleString.fromByteArrayUncached(value.getBytes(StandardCharsets.UTF_8), TruffleString.Encoding.UTF_8);
    }

    @Override
    public TruffleString executeString(VirtualFrame frame) {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitStringValue(this);
    }
}
