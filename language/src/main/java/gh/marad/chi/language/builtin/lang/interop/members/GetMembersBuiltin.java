package gh.marad.chi.language.builtin.lang.interop.members;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.StopIterationException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.Types;
import gh.marad.chi.language.ChiArgs;
import gh.marad.chi.language.builtin.lang.interop.LangInteropBuiltin;
import gh.marad.chi.language.image.NodeId;
import gh.marad.chi.language.runtime.ChiArray;
import gh.marad.chi.language.runtime.TODO;

public class GetMembersBuiltin extends LangInteropBuiltin {
    @Child
    private InteropLibrary library;

    public GetMembersBuiltin() {
        this.library = InteropLibrary.getFactory().createDispatched(3);
    }

    @Override
    public FunctionType type() {
        return Types.fn(Types.getAny(), Types.getBool(), Types.array(Types.getString()));
    }

    @Override
    public String name() {
        return "getMembers";
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            var receiver = ChiArgs.getObjectAndUnwrapHostSymbol(frame, 0);
            var includeInternal = ChiArgs.getBoolean(frame, 1);
            var members = library.getMembers(receiver, includeInternal);
            var size = library.getArraySize(members);
            var data = new TruffleString[(int) size];
            var iter = library.getIterator(members);
            var i = 0;
            while(library.hasIteratorNextElement(iter)) {
                try {
                    var element = (String) library.getIteratorNextElement(iter);
                    // TODO: use cached truffle string conversion node
                    data[i++] = TruffleString.fromJavaStringUncached(element, TruffleString.Encoding.UTF_8);
                } catch (StopIterationException e) {
                    throw new TODO(e);
                }
            }
            return new ChiArray(data, Types.getString());
        } catch (UnsupportedMessageException e) {
            throw new TODO(e);
        }
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.GetMembersBuitlin;
    }
}
