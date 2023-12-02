package gh.marad.chi.language.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import gh.marad.chi.core.Type;

import java.util.Arrays;

@ExportLibrary(InteropLibrary.class)
public class ChiArray implements ChiValue {
    private final Object[] array;
    private final Type elementType;

    public ChiArray(int capacity, Object defaultValue, Type type) {
        array = new Object[capacity];
        this.elementType = type;
        Arrays.fill(array, defaultValue);
    }

    public ChiArray(int capacity, Type type) {
        array = new Object[capacity];
        this.elementType = type;
    }

    public ChiArray(Object[] array, Type type) {
        this.array = array;
        this.elementType = type;
    }

    public Type getType() {
        return Type.array(elementType);
    }

    public Type getElementType() {
        return elementType;
    }

    public Object[] unsafeGetUnderlayingArray() {
        return array;
    }

    @ExportMessage
    public boolean hasArrayElements() {
        return true;
    }

    @ExportMessage
    public boolean isArrayElementReadable(long index) {
        return withinBounds(index);
    }

    @ExportMessage
    public Object readArrayElement(long index) throws InvalidArrayIndexException {
        assertIndexValid(index);
        return array[(int) index];
    }

    @ExportMessage
    public long getArraySize() {
        return array.length;
    }

    @ExportMessage
    public boolean isArrayElementModifiable(long index) {
        return withinBounds(index);
    }

    @ExportMessage
    public boolean isArrayElementInsertable(long index) {
        return false;
    }

    @ExportMessage
    public void writeArrayElement(long index, Object value) throws InvalidArrayIndexException {
        assertIndexValid(index);
        array[(int) index] = value;
    }

    @ExportMessage
    @Override
    @CompilerDirectives.TruffleBoundary
    public Object toDisplayString(boolean allowSideEffects) {
        var sb = new StringBuilder();
        sb.append("arrayOf(");
        var index = 0;
        for (Object element : array) {
            sb.append(element.toString());
            if (index < array.length - 1) {
                sb.append(", ");
            }
            index += 1;
        }
        sb.append(")");
        return sb.toString();
    }

    private boolean withinBounds(long index) {
        return 0 <= index && index < array.length;
    }

    private void assertIndexValid(long index) throws InvalidArrayIndexException {
        if (index < 0 || index >= array.length) {
            CompilerDirectives.transferToInterpreter();
            throw InvalidArrayIndexException.create(index);
        }
    }

    @ExportMessage
    @Override
    public boolean hasLanguage() {
        return ChiValue.super.hasLanguage();
    }

    @ExportMessage
    @Override
    public Class<? extends TruffleLanguage<?>> getLanguage() {
        return ChiValue.super.getLanguage();
    }
}
