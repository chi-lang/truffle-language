package gh.marad.chi.language.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import gh.marad.chi.core.Type;

import java.util.ArrayList;
import java.util.Objects;

@ExportLibrary(InteropLibrary.class)
public class ChiArray implements ChiValue {
    private final ArrayList<Object> array;
    private final Type elementType;

    public ChiArray(Type type) {
        this.array = new ArrayList<>();
        this.elementType = type;
    }

    public ChiArray(int capacity, Object defaultValue, Type type) {
        this(capacity, type);
        for (int i = 0; i < capacity; i++) {
            this.array.add(i, defaultValue);
        }
    }

    public ChiArray(int capacity, Type type) {
        this.array = new ArrayList<>(capacity);
        this.elementType = type;
    }

    public ChiArray(Object[] array, Type elementType) {
        this(array.length, elementType);
        for (int i = 0; i < array.length; i++) {
            this.array.add(i, array[i]);
        }
    }

    public ChiArray(ArrayList<Object> array, Type elementType) {
        this.array = array;
        this.elementType = elementType;
    }

    public void add(Object element) {
        array.add(element);
    }

    public void add(int index, Object element) {
        array.add(index, element);
    }

    public void removeAt(int index) {
        array.remove(index);
    }

    public void remove(Object object) {
        array.remove(object);
    }

    public void clear() {
        array.clear();
    }

    public Type getType() {
        return Type.array(elementType);
    }

    public Type getElementType() {
        return elementType;
    }

    public ArrayList<Object> getUnderlayingArrayList() {
        return array;
    }

    public Object[] copyToJavaArray() {
        return array.toArray();
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
        return array.get((int) index);
    }

    @ExportMessage
    public long getArraySize() {
        return array.size();
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
        array.set((int) index, value);
    }

    @ExportMessage
    @CompilerDirectives.TruffleBoundary
    @Override
    public Object toDisplayString(boolean allowSideEffects,
                                  @CachedLibrary(limit = "3") InteropLibrary interopLibrary) {
        var sb = new StringBuilder();
        sb.append("[");
        var iter = array.iterator();
        while (iter.hasNext()) {
            sb.append(interopLibrary.toDisplayString(iter.next(), allowSideEffects));
            if (iter.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private boolean withinBounds(long index) {
        return 0 <= index && index < array.size();
    }

    private void assertIndexValid(long index) throws InvalidArrayIndexException {
        if (index < 0 || index >= array.size()) {
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

    @Override
    public int hashCode() {
        return array.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ChiArray chiArray = (ChiArray) object;
        return Objects.equals(array, chiArray.array) && Objects.equals(elementType, chiArray.elementType);
    }
}
