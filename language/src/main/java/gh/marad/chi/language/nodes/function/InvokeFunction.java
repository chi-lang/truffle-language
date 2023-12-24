package gh.marad.chi.language.nodes.function;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.ExpressionNode;
import gh.marad.chi.language.runtime.TODO;

public class InvokeFunction extends ExpressionNode {
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    public ChiNode function;
    @Children
    public final ChiNode[] arguments;
    @SuppressWarnings("FieldMayBeFinal")
    @Child
    private InteropLibrary library;


    public InvokeFunction(ChiNode function, ChiNode[] arguments) {
        this.function = function;
        this.arguments = arguments;
        library = InteropLibrary.getFactory().createDispatched(3);
    }

    @Override
    @ExplodeLoop
    public Object executeGeneric(VirtualFrame frame) {
        try {
            var fn = function.executeFunction(frame);

            CompilerAsserts.compilationConstant(arguments.length);
            Object[] args = new Object[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                args[i] = arguments[i].executeGeneric(frame);
            }
            return library.execute(fn, args);
        } catch (UnsupportedTypeException | ArityException | UnsupportedMessageException e) {
            CompilerDirectives.transferToInterpreter();
            throw new RuntimeException(e);
        } catch (UnexpectedResultException ex) {
            throw new TODO(ex);
        }
    }

    @Override
    public boolean hasTag(Class<? extends Tag> tag) {
        return tag == StandardTags.CallTag.class || super.hasTag(tag);
    }

    @Override
    public void accept(ChiNodeVisitor visitor) throws Exception {
        visitor.visitInvokeFunction(this);
        for (ChiNode argument : arguments) {
            argument.accept(visitor);
        }
        function.accept(visitor);
    }
}
