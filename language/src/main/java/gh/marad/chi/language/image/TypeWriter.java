package gh.marad.chi.language.image;

import gh.marad.chi.core.TypeAlias;
import gh.marad.chi.core.types.*;
import gh.marad.chi.core.types.Record;
import gh.marad.chi.language.runtime.TODO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeWriter {

    public static void writeTypeAlias(TypeAlias typeAlias, DataOutputStream stream) throws IOException {
        stream.writeUTF(typeAlias.getTypeId().getModuleName());
        stream.writeUTF(typeAlias.getTypeId().getPackageName());
        stream.writeUTF(typeAlias.getTypeId().getName());
        writeType(typeAlias.getType(), stream);
    }

    public static TypeAlias readTypeAlias(DataInputStream stream) throws IOException {

        return new TypeAlias(
                new gh.marad.chi.core.types.TypeId(
                        stream.readUTF(),
                        stream.readUTF(),
                        stream.readUTF()
                ),
                readType(stream)
        );
    }

    public static void writeTypes(List<Type> types, DataOutputStream stream) throws IOException {
        stream.writeByte(types.size());
        for (Type type : types) {
            writeType(type, stream);
        }
    }

    public static void writeTypes(Type[] types, DataOutputStream stream) throws IOException {
        stream.writeByte(types.length);
        for (Type type : types) {
            writeType(type, stream);
        }
    }

    public static void writeStrings(List<String> strings, DataOutputStream stream) throws IOException {
        stream.writeShort(strings.size());
        for (String string : strings) {
            stream.writeUTF(string);
        }
    }

    public static void writeVariables(List<Variable> typeVars, DataOutputStream stream) throws IOException {
        stream.writeByte(typeVars.size());
        for (Variable typeVar : typeVars) {
            stream.writeUTF(typeVar.getName());
            stream.writeShort(typeVar.getLevel());
        }
    }

    public static void writeType(TypeScheme type, DataOutputStream stream) throws IOException {
        if (type instanceof Primitive) {
            if (Type.getAny().equals(type)) {
                stream.writeByte(TypeId.Any.id());
            } else if (Type.getBool().equals(type)) {
                stream.writeByte(TypeId.Bool.id());
            } else if (Type.getFloat().equals(type)) {
                stream.writeByte(TypeId.Float.id());
            } else if (Type.getInt().equals(type)) {
                stream.writeByte(TypeId.Int.id());
            } else if (Type.getString().equals(type)) {
                stream.writeByte(TypeId.String.id());
            } else if (Type.getUnit().equals(type)) {
                stream.writeByte(TypeId.Unit.id());
            }
        } else if (type instanceof Record t) {
            stream.writeByte(TypeId.Record.id());
            writeTypeId(t.getTypeId(), stream);
            stream.writeShort(t.getFields().size());
            for (var field : t.getFields()) {
                stream.writeUTF(field.getName());
                writeType(field.getType(), stream);
            }
            writeStrings(t.typeParams(), stream);
        } else if (type instanceof Sum t) {
            stream.writeByte(TypeId.Sum.id());
            writeTypeId(t.getTypeId(), stream);
            writeType(t.getLhs(), stream);
            writeType(t.getRhs(), stream);
            writeStrings(t.typeParams(), stream);
        } else if (type instanceof Function t) {
            stream.writeByte(TypeId.Fn.id());
            writeTypes(t.getTypes(), stream);
            writeStrings(t.getTypeParams(), stream);
        } else if (type instanceof Variable t) {
            stream.writeByte(TypeId.TypeVariable.id());
            stream.writeUTF(t.getName());
            stream.writeShort(t.getLevel());
        } else if (type instanceof Array t) {
            stream.writeByte(TypeId.Array.id());
            writeType(t.getElementType(), stream);
            writeStrings(t.typeParams(), stream);
        } else if (type instanceof PolyType t) {
            stream.writeByte(TypeId.TypeScheme.id());
            stream.writeShort(t.getLevel());
            writeType(t.getBody(), stream);
        } else {
            throw new TODO("Unsupported type " + type);
        }
    }

    public static void writeTypeId(gh.marad.chi.core.types.TypeId id, DataOutputStream stream) throws IOException {
        var hasId = id != null;
        stream.writeBoolean(hasId);
        if (hasId) {
            stream.writeUTF(id.getModuleName());
            stream.writeUTF(id.getPackageName());
            stream.writeUTF(id.getName());
        }
    }

    public static gh.marad.chi.core.types.TypeId readTypeId(DataInputStream stream) throws IOException {
        if (stream.readBoolean()) {
            return new gh.marad.chi.core.types.TypeId(
                    stream.readUTF(),
                    stream.readUTF(),
                    stream.readUTF()
            );
        } else {
            return null;
        }
    }

    public static TypeScheme[] readTypeSchemes(DataInputStream stream) throws IOException {
        var count = stream.readByte();
        var types = new TypeScheme[count];

        for (byte i = 0; i < count; i++) {
            types[i] = readTypeScheme(stream);
        }
        return types;
    }

    public static Type[] readTypes(DataInputStream stream) throws IOException {
        var count = stream.readByte();
        var types = new Type[count];

        for (byte i = 0; i < count; i++) {
            types[i] = (Type) readTypeScheme(stream);
        }
        return types;
    }

    public static Type readType(DataInputStream stream) throws IOException {
        return (Type) readTypeScheme(stream);
    }

    public static TypeScheme readTypeScheme(DataInputStream stream) throws IOException {
        var typeId = TypeId.fromId(stream.readByte());
        return switch (typeId) {
            case Any -> Type.getAny();
            case Bool -> Type.getBool();
            case Float -> Type.getFloat();
            case Int -> Type.getInt();
            case String -> Type.getString();
            case Unit -> Type.getUnit();
            case Record ->
                new Record(readTypeId(stream), readFields(stream), readStrings(stream));

            case Sum ->
                    new Sum(
                            readTypeId(stream),
                            readType(stream),
                            readType(stream),
                            readStrings(stream));

            case Fn -> new Function(
                    Arrays.stream(readTypes(stream)).toList(),
                    readStrings(stream)
            );
            case Array -> new Array(readType(stream), readStrings(stream));
            case TypeVariable -> new Variable(stream.readUTF(), stream.readShort());
            case TypeScheme -> new PolyType(stream.readShort(), readType(stream));
        };
    }

    private static List<Record.Field> readFields(DataInputStream stream) throws IOException {
        var fieldCount = stream.readShort();
        var fields = new ArrayList<Record.Field>(fieldCount);
        for (int i = 0; i < fieldCount; i++) {
            fields.add(new Record.Field(
                    stream.readUTF(),
                    readType(stream)
            ));
        }
        return fields;
    }

    private static List<String> readStrings(DataInputStream stream) throws IOException {
        var count = stream.readShort();
        var result = new ArrayList<String>(count);
        for (int i = 0; i < count; i++) {
            result.add(stream.readUTF());
        }
        return result;
    }
}

