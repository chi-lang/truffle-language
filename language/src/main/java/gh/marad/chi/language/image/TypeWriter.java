package gh.marad.chi.language.image;

import gh.marad.chi.core.namespace.TypeInfo;
import gh.marad.chi.core.namespace.VariantField;
import gh.marad.chi.core.types.*;
import gh.marad.chi.language.runtime.TODO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeWriter {
    public static void writeTypeInfo(TypeInfo typeInfo, DataOutputStream stream) throws IOException {
        stream.writeUTF(typeInfo.getModuleName());
        stream.writeUTF(typeInfo.getPackageName());
        stream.writeUTF(typeInfo.getName());
        writeType(typeInfo.getType(), stream);
        stream.writeBoolean(typeInfo.isPublic());
        stream.writeByte(typeInfo.getFields().size());
        for (VariantField field : typeInfo.getFields()) {
            stream.writeUTF(field.getName());
            writeType(field.getType(), stream);
            stream.writeBoolean(field.getPublic());
        }
    }

    public static TypeInfo readTypeInfo(DataInputStream stream) throws IOException {
        return new TypeInfo(
                stream.readUTF(),
                stream.readUTF(),
                stream.readUTF(),
                readType(stream),
                stream.readBoolean(),
                readFields(stream)
        );
    }

    private static List<VariantField> readFields(DataInputStream stream) throws IOException {
        var count = stream.readByte();
        var result = new ArrayList<VariantField>(count);
        for (int i = 0; i < count; i++) {
            result.add(new VariantField(
                    stream.readUTF(),
                    readType(stream),
                    stream.readBoolean()
            ));
        }
        return result;
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

    public static void writeTypeVariables(List<TypeVariable> typeVars, DataOutputStream stream) throws IOException {
        stream.writeByte(typeVars.size());
        for (TypeVariable typeVar : typeVars) {
            stream.writeUTF(typeVar.getName());
        }
    }

    public static void writeType(Type type, DataOutputStream stream) throws IOException {
        if (type instanceof SimpleType t) {
            if (Types.getAny().equals(type)) {
                stream.writeByte(TypeId.Any.id());
            } else if (Types.getBool().equals(type)) {
                stream.writeByte(TypeId.Bool.id());
            } else if (Types.getFloat().equals(type)) {
                stream.writeByte(TypeId.Float.id());
            } else if (Types.getInt().equals(type)) {
                stream.writeByte(TypeId.Int.id());
            } else if (Types.getString().equals(type)) {
                stream.writeByte(TypeId.String.id());
            } else if (Types.getUnit().equals(type)) {
                stream.writeByte(TypeId.Unit.id());
            } else {
                stream.writeByte(TypeId.Simple.id());
                stream.writeUTF(t.getModuleName());
                stream.writeUTF(t.getPackageName());
                stream.writeUTF(t.getName());
            }
        } else if (type instanceof ProductType t) {
            stream.writeByte(TypeId.Product.id());
            stream.writeUTF(t.getModuleName());
            stream.writeUTF(t.getPackageName());
            stream.writeUTF(t.getName());
            writeTypes(t.getTypes(), stream);
            writeTypes(t.getTypeParams(), stream);
            writeTypeVariables(t.getTypeSchemeVariables(), stream);
        } else if (type instanceof SumType t) {
            stream.writeByte(TypeId.Sum.id());
            stream.writeUTF(t.getModuleName());
            stream.writeUTF(t.getPackageName());
            stream.writeUTF(t.getName());
            writeTypes(t.getTypeParams(), stream);
            stream.writeShort(t.getSubtypes().size());
            for (String subtype : t.getSubtypes()) {
                stream.writeUTF(subtype);
            }
            writeTypeVariables(t.typeSchemeVariables(), stream);
        } else if (type instanceof FunctionType t) {
            stream.writeByte(TypeId.Fn.id());
            writeTypes(t.getTypes(), stream);
            writeTypeVariables(t.getTypeSchemeVariables(), stream);
        } else if (type instanceof TypeVariable t) {
            stream.writeByte(TypeId.TypeVariable.id());
            stream.writeUTF(t.getName());
        } else {
            throw new TODO("Unsupported type " + type);
        }
    }

    public static Type[] readTypes(DataInputStream stream) throws IOException {
        var count = stream.readByte();
        var types = new Type[count];

        for (byte i = 0; i < count; i++) {
            types[i] = readType(stream);
        }
        return types;
    }

    public static List<TypeVariable> readTypeVariables(DataInputStream stream) throws IOException {
        var count = stream.readByte();
        var typeVars = new ArrayList<TypeVariable>();
        for (int i = 0; i < count; i++) {
            typeVars.add(new TypeVariable(stream.readUTF()));
        }
        return typeVars;
    }


    public static Type readType(DataInputStream stream) throws IOException {
        var typeId = TypeId.fromId(stream.readByte());
        return switch (typeId) {
            case Any -> Types.getAny();
            case Bool -> Types.getBool();
            case Float -> Types.getFloat();
            case Int -> Types.getInt();
            case String -> Types.getString();
            case Unit -> Types.getUnit();
            case Simple -> new SimpleType(
                    stream.readUTF(),
                    stream.readUTF(),
                    stream.readUTF()
            );
            case Product -> new ProductType(
                    stream.readUTF(),
                    stream.readUTF(),
                    stream.readUTF(),
                    Arrays.stream(readTypes(stream)).toList(),
                    Arrays.stream(readTypes(stream)).toList(),
                    readTypeVariables(stream)
            );
            case Sum -> new SumType(
                    stream.readUTF(),
                    stream.readUTF(),
                    stream.readUTF(),
                    Arrays.stream(readTypes(stream)).toList(),
                    readStrings(stream),
                    readTypeVariables(stream)
            );
            case Fn -> new FunctionType(
                    Arrays.stream(readTypes(stream)).toList(),
                    readTypeVariables(stream)
            );
            case TypeVariable -> new TypeVariable(stream.readUTF());
            default -> throw new IllegalStateException("Unexpected value: " + typeId);
        };
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

