package gh.marad.chi.language.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;
import com.oracle.truffle.api.object.Shape;
import com.oracle.truffle.api.utilities.TriState;
import gh.marad.chi.core.types.HasTypeId;
import gh.marad.chi.core.types.Type;

import java.util.Objects;

@ExportLibrary(InteropLibrary.class)
public class ChiObject extends DynamicObject implements ChiValue {
    private final Type type;

    private final TruffleLanguage.Env env;

    public ChiObject(Type type, Shape shape, TruffleLanguage.Env env) {
        super(shape);
        this.type = type;
        this.env = env;
    }

    public Type getType() {
        return type;
    }

    @ExportMessage
    boolean hasMembers() {
        return getShape().getPropertyCount() > 0;
    }

    @ExportMessage
    Object readMember(String name,
                      @CachedLibrary("this") DynamicObjectLibrary objectLibrary) throws UnknownIdentifierException {
        Object result = objectLibrary.getOrDefault(this, name, null);
        if (result == null) {
            throw UnknownIdentifierException.create(name);
        }
        return result;
    }

    @ExportMessage
    public void writeMember(String name, Object value,
                     @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        objectLibrary.put(this, name, value);
    }

    @ExportMessage
    public boolean isMemberReadable(String member,
                             @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return objectLibrary.containsKey(this, member);
    }

    @ExportMessage
    public Object getMembers(boolean includeInternal,
                      @CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        return new ChiArray(objectLibrary.getKeyArray(this), Type.getString());
    }

    @ExportMessage
    public boolean isMemberModifiable(String member) {
        return getShape().hasProperty(member);
    }

    @ExportMessage
    public boolean isMemberInsertable(String member) {
        return !getShape().hasProperty(member);
    }

    @ExportMessage
    public boolean isMemberInvocable(String member,
                              @CachedLibrary("this") @Cached.Exclusive DynamicObjectLibrary objectLibrary,
                              @CachedLibrary(limit = "3") @Cached.Exclusive InteropLibrary interop) {
        try {
            return isMemberReadable(member, objectLibrary)
                           && interop.isExecutable(readMember(member, objectLibrary));
        } catch (UnknownIdentifierException e) {
            throw new TODO(e);
        }
    }

    @ExportMessage
    public Object invokeMember(String member,
                               Object[] arguments,
                               @CachedLibrary("this") @Cached.Exclusive DynamicObjectLibrary objectLibrary,
                               @CachedLibrary(limit = "3") @Cached.Exclusive InteropLibrary interop) {
        try {
            return interop.execute(readMember(member, objectLibrary), arguments);
        } catch (UnsupportedTypeException | ArityException | UnsupportedMessageException |
                 UnknownIdentifierException e) {
            throw new TODO(e);
        }
    }

    @ExportMessage
    @CompilerDirectives.TruffleBoundary
    public Object toDisplayString(boolean allowSideEffects,
                                  @CachedLibrary("this") @Cached.Exclusive DynamicObjectLibrary objectLibrary,
                                  @CachedLibrary(limit = "3") @Cached.Exclusive InteropLibrary interopLibrary) {
        var sb = new StringBuilder();
        if (type instanceof HasTypeId t && t.getTypeId() != null) {
            sb.append(t.getTypeId().getName());
        }
        sb.append("{ ");
        var index = 0;
        var fieldNames = objectLibrary.getKeyArray(this);
        for (var key : fieldNames) {
            var value = objectLibrary.getOrDefault(this, key, "");
            sb.append(key);
            sb.append(": ");
            sb.append(interopLibrary.toDisplayString(value));
            if (index < fieldNames.length - 1) {
                sb.append(", ");
            }
            index += 1;
        }
        sb.append(" }");
        return sb.toString();
    }

    @Override
    public String toString() {
        return (String) toDisplayString(false,
                DynamicObjectLibrary.getUncached(),
                InteropLibrary.getUncached());
    }

    @ExportMessage
    static final class IsIdenticalOrUndefined {
        @Specialization
        @CompilerDirectives.TruffleBoundary
        static TriState doChiObject(ChiObject receiver, ChiObject other,
                                    @CachedLibrary("receiver") DynamicObjectLibrary objectLibrary) {
            var recvShape = objectLibrary.getShape(receiver);
            var otherShape = objectLibrary.getShape(other);
            if (recvShape.equals(otherShape)) {
                var equal = true;
                for (var key : objectLibrary.getKeyArray(receiver)) {
                    var thisField = objectLibrary.getOrDefault(receiver, key, null);
                    var otherField = objectLibrary.getOrDefault(other, key, null);
                    if (receiver.env.isHostObject(thisField) && receiver.env.isHostObject(otherField)) {
                        equal = equal && receiver.env.asHostObject(thisField)
                                                     .equals(receiver.env.asHostObject(otherField));
                    } else if(thisField instanceof ChiObject && otherField instanceof ChiObject) {
                        var thisIop = InteropLibrary.getUncached(thisField);
                        var otherIop = InteropLibrary.getUncached(otherField);
                        equal = equal && thisIop.isIdentical(thisField, otherField, otherIop);
                    } else {
                        equal = equal && thisField.equals(otherField);
                    }
                }
                return equal ? TriState.TRUE : TriState.FALSE;
            } else {
                return TriState.FALSE;
            }
        }

        @Specialization
        static TriState doOther(ChiObject receiver, Object other) {
            return TriState.UNDEFINED;
        }
    }

    @ExportMessage
    @CompilerDirectives.TruffleBoundary
    public int identityHashCode(@CachedLibrary("this") DynamicObjectLibrary objectLibrary) {
        var members = objectLibrary.getKeyArray(this);
        var values = new Object[members.length];
        var i = 0;
        for (var key : members) {
            values[i++] = objectLibrary.getOrDefault(this, key, null);
        }
        return Objects.hash(values);
    }
}
