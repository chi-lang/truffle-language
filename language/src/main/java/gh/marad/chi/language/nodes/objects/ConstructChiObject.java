package gh.marad.chi.language.nodes.objects;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import gh.marad.chi.core.types.Record;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.ChiLanguage;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

public class ConstructChiObject extends ExpressionNode {
    private final InteropLibrary interopLibrary;
    private final Record type;
    private final String[] fields;
    private final ChiNode[] values;

    public ConstructChiObject(Record type, String[] fields, ChiNode[] values) {
        this.type = type;
        this.fields = fields;
        this.values = values;
        assert fields.length == values.length;
        interopLibrary = InteropLibrary.getUncached();
    }

    public String[] getFields() {
        return fields;
    }

    public ChiNode[] getValues() {
        return values;
    }

    public Record getType() {
        return type;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var env = ChiContext.get(this).getEnv();
        var object = ChiLanguage.createObject(type, env);
        for (int i = 0; i < fields.length; i++) {
            try {
                interopLibrary.writeMember(object, fields[i], values[i].executeGeneric(frame));
            } catch (UnsupportedMessageException | UnsupportedTypeException | UnknownIdentifierException e) {
                CompilerDirectives.transferToInterpreter();
                throw new RuntimeException(e);
            }
        }
        return object;
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitConstructChiObject(this);
        for (ChiNode value : values) {
            value.accept(visitor);
        }
    }
}
