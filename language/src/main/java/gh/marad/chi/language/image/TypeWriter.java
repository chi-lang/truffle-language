package gh.marad.chi.language.image;

import gh.marad.chi.core.types.*;
import gh.marad.chi.language.runtime.TODO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class TypeWriter {
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

    public static void writeType(Type type, DataOutputStream stream) throws IOException {
        if (type instanceof SimpleType) {

        } else if (type instanceof ProductType) {

        } else if (type instanceof SumType) {

        } else if (type instanceof FunctionType) {

        } else if (type instanceof TypeVariable) {

        } else {
            throw new TODO("Unsupported type " + type);
        }

//        if (type instanceof AnyType) {
//            stream.writeByte(TypeId.Any.id());
//        } else if (type instanceof BoolType) {
//            stream.writeByte(TypeId.Bool.id());
//        } else if (type instanceof FloatType) {
//            stream.writeByte(TypeId.Float.id());
//        } else if (type instanceof IntType) {
//            stream.writeByte(TypeId.Int.id());
//        } else if (type instanceof StringType) {
//            stream.writeByte(TypeId.String.id());
//        } else if (type instanceof UndefinedType) {
//            stream.writeByte(TypeId.Undefined.id());
//        } else if (type instanceof UnitType) {
//            stream.writeByte(TypeId.Unit.id());
//        } else if (type instanceof ArrayType arrayType) {
//            stream.writeByte(TypeId.Array.id());
//            writeType(arrayType.getElementType(), stream);
//        } else if (type instanceof FnType fn) {
//            if (fn.isTypeConstructor()) {
//                stream.writeByte(TypeId.GenericFn.id());
//                writeTypes(fn.getGenericTypeParameters().toArray(new Type[0]), stream);
//                writeType(fn.getReturnType(), stream);
//                writeTypes(fn.getParamTypes(), stream);
//            } else {
//                stream.writeByte(TypeId.Fn.id());
//                writeType(fn.getReturnType(), stream);
//                writeTypes(fn.getParamTypes(), stream);
//            }
//        } else if (type instanceof GenericTypeParameter typeParameter) {
//            stream.writeByte(TypeId.GenericTypeParameter.id());
//            stream.writeUTF(typeParameter.getName());
//        } else if (type instanceof VariantType variantType) {
//            stream.writeByte(TypeId.Variant.id());
//            writeVariantType(stream, variantType);
//        } else {
//            throw new TODO("Unsupported type " + type);
//        }
    }

//    public static void writeVariant(VariantType.Variant variant, DataOutputStream stream) throws IOException {
//        stream.writeBoolean(variant.getPublic());
//        stream.writeUTF(variant.getVariantName());
//
//        // write variant fields
//        var fields = variant.getFields();
//        stream.writeByte(fields.size());
//        for (VariantType.VariantField field : fields) {
//            stream.writeBoolean(field.getPublic());
//            stream.writeUTF(field.getName());
//            writeType(field.getType(), stream);
//        }
//    }
//

    public static Type[] readTypes(DataInputStream stream) throws IOException {
        var count = stream.readByte();
        var types = new Type[count];

        for (byte i = 0; i < count; i++) {
            types[i] = readType(stream);
        }
        return types;
    }

    public static Type readType(DataInputStream stream) throws IOException {
        var typeId = TypeId.fromId(stream.readByte());
        return switch (typeId) {
            case Bool -> Types.getBool();
            case Float -> Types.getFloat();
            case Int -> Types.getInt();
            case Any -> Types.getAny();
            case String -> Types.getString();
            case Unit -> Types.getUnit();
            case Array -> Types.array(readType(stream));
//            case Fn -> Types.fn(
//                    readType(stream),
//                    readTypes(stream)
//            );

            default -> throw new IllegalStateException("Unexpected value: " + typeId);
        };
    }
}

