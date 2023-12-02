package gh.marad.chi.language.image;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.core.VariantType;
import gh.marad.chi.language.ChiLanguage;
import gh.marad.chi.language.ChiTypes;
import gh.marad.chi.language.ChiTypesGen;
import gh.marad.chi.language.runtime.ChiArray;
import gh.marad.chi.language.runtime.ChiObject;
import gh.marad.chi.language.runtime.ChiObjectGen;
import gh.marad.chi.language.runtime.TODO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ValueWriter {

    public static void writeValue(Object value, DataOutputStream stream) throws IOException, UnsupportedMessageException, UnknownIdentifierException {
        if (value instanceof Long v) {
            stream.writeByte(TypeId.Int.id());
            stream.writeLong(v);
        } else if (value instanceof Float f) {
            stream.writeByte(TypeId.Float.id());
            stream.writeFloat(f);
        } else if (value instanceof Boolean b) {
            stream.writeByte(TypeId.Bool.id());
            stream.writeBoolean(b);
        } else if (value instanceof TruffleString s) {
            stream.writeByte(TypeId.String.id());
            stream.writeUTF(s.toJavaStringUncached());
        } else if (value instanceof ChiObject o) {
            stream.writeByte(TypeId.Variant.id());
            TypeWriter.writeType(o.getType(), stream);
            var interop = ChiObjectGen.InteropLibraryExports.Cached.getUncached();
            var members = ChiTypesGen.asChiArray(interop.getMembers(o)).unsafeGetUnderlayingArray();
            stream.writeInt(members.length);
            for (Object member : members) {
                var fieldName = (String) member;
                var fieldValue = interop.readMember(o, fieldName);
                stream.writeUTF(fieldName);
                writeValue(fieldValue, stream);
            }
        } else if (value instanceof ChiArray a) {
            stream.writeByte(TypeId.Array.id());
            TypeWriter.writeType(a.getElementType(), stream);
            var items = a.unsafeGetUnderlayingArray();
            stream.writeInt(items.length);
            for (Object o : items) {
                writeValue(o, stream);
            }
        } else {
            throw new TODO("Cannot write unsupported value of type '%s'".formatted(ChiTypes.getType(value)));
        }
    }

    private static Object readVariantType(DataInputStream stream, TruffleLanguage.Env env) throws Exception {
        var interop = ChiObjectGen.InteropLibraryExports.Uncached.getUncached();
        var type = (VariantType) TypeWriter.readType(stream);
        var obj = ChiLanguage.createObject(type, env);
        var fieldCount = stream.readInt();
        for (int i = 0; i < fieldCount; i++) {
            var fieldName = stream.readUTF();
            var fieldValue = readValue(stream, env);
            interop.writeMember(obj, fieldName, fieldValue);
        }

        return obj;
    }

    public static Object readValue(DataInputStream stream, TruffleLanguage.Env env) throws Exception {
        var typeId = TypeId.fromId(stream.readByte());
        return switch (typeId) {
            case Bool -> stream.readBoolean();
            case Float -> stream.readFloat();
            case Int -> stream.readLong();
            case String -> TruffleString.fromJavaStringUncached(stream.readUTF(), TruffleString.Encoding.UTF_8);
            case Array -> readArray(stream, env);
            case Variant -> readVariantType(stream, env);
            default -> throw new TODO("Cannot read type id: " + typeId);
        };
    }

    private static Object readArray(DataInputStream stream, TruffleLanguage.Env env) throws Exception {
        var type = TypeWriter.readType(stream);
        var elementCount = stream.readInt();
        var arr = new Object[elementCount];
        for (int i = 0; i < elementCount; i++) {
            arr[i] = readValue(stream, env);
        }
        return new ChiArray(arr, type);
    }
}
