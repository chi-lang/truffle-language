package gh.marad.chi.language.nodes.objects;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import gh.marad.chi.core.VariantType;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.ChiLanguage;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;

import java.util.Objects;

public class ConstructChiObject extends ExpressionNode {
    private final String[] fieldNames;
    private final InteropLibrary interopLibrary;
    public final VariantType type;

    // TODO this should only have variant type identifier, and types should be defined in central place
    // TODO runtime should also have some different representation of the type that references the Chi type
    //      because serialization of VariantType that has fields of other VariantTypes is VERY HEAVY

    public ConstructChiObject(VariantType type) {
        this.fieldNames = Objects.requireNonNull(type.getVariant()).getFields().stream()
                                 .map(VariantType.VariantField::getName).toList().toArray(new String[0]);
        this.type = type;
        interopLibrary = InteropLibrary.getUncached();
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var env = ChiContext.get(this).getEnv();
        var language = ChiLanguage.get(this);
        var object = language.createObject(fieldNames, type, env);
        for (int i = 0; i < fieldNames.length; i++) {
            try {
                interopLibrary.writeMember(object, fieldNames[i], ChiArgs.getObject(frame, i));
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
    }
}
