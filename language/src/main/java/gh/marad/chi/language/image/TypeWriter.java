package gh.marad.chi.language.image;

import gh.marad.chi.core.*;
import gh.marad.chi.language.runtime.TODO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        if (type instanceof AnyType) {
            stream.writeByte(TypeId.Any.id());
        } else if (type instanceof BoolType) {
            stream.writeByte(TypeId.Bool.id());
        } else if (type instanceof FloatType) {
            stream.writeByte(TypeId.Float.id());
        } else if (type instanceof IntType) {
            stream.writeByte(TypeId.Int.id());
        } else if (type instanceof StringType) {
            stream.writeByte(TypeId.String.id());
        } else if (type instanceof UndefinedType) {
            stream.writeByte(TypeId.Undefined.id());
        } else if (type instanceof UnitType) {
            stream.writeByte(TypeId.Unit.id());
        } else if (type instanceof ArrayType arrayType) {
            stream.writeByte(TypeId.Array.id());
            writeType(arrayType.getElementType(), stream);
        } else if (type instanceof FnType fn) {
            if (fn.isTypeConstructor()) {
                stream.writeByte(TypeId.GenericFn.id());
                writeTypes(fn.getGenericTypeParameters().toArray(new Type[0]), stream);
                writeType(fn.getReturnType(), stream);
                writeTypes(fn.getParamTypes(), stream);
            } else {
                stream.writeByte(TypeId.Fn.id());
                writeType(fn.getReturnType(), stream);
                writeTypes(fn.getParamTypes(), stream);
            }
        } else if (type instanceof GenericTypeParameter typeParameter) {
            stream.writeByte(TypeId.GenericTypeParameter.id());
            stream.writeUTF(typeParameter.getName());
        } else if (type instanceof VariantType variantType) {
            stream.writeByte(TypeId.Variant.id());
            writeVariantType(stream, variantType);
        } else {
            throw new TODO("Unsupported type " + type);
        }
    }

    private static void writeVariantType(DataOutputStream stream, VariantType type) throws IOException {
        stream.writeUTF(type.getModuleName());
        stream.writeUTF(type.getPackageName());
        stream.writeUTF(type.getSimpleName());
        writeTypes(type.getGenericTypeParameters().toArray(new Type[0]), stream);

        // write concrete type map
        var concreteTypes = type.getConcreteTypeParameters();
        var concreteTypeCount = type.getConcreteTypeParameters().size();
        stream.writeByte(concreteTypeCount);
        for (GenericTypeParameter parameter : concreteTypes.keySet()) {
            var value = concreteTypes.get(parameter);
            writeType(parameter, stream);
            writeType(value, stream);
        }

        // write variant
        var variant = type.getVariant();
        if (variant != null) {
            stream.writeBoolean(true);
            writeVariant(variant, stream);
        } else {
            stream.writeBoolean(false);
        }
    }

    public static void writeVariant(VariantType.Variant variant, DataOutputStream stream) throws IOException {
        stream.writeBoolean(variant.getPublic());
        stream.writeUTF(variant.getVariantName());

        // write variant fields
        var fields = variant.getFields();
        stream.writeByte(fields.size());
        for (VariantType.VariantField field : fields) {
            stream.writeBoolean(field.getPublic());
            stream.writeUTF(field.getName());
            writeType(field.getType(), stream);
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

    public static Type readType(DataInputStream stream) throws IOException {
        var typeId = TypeId.fromId(stream.readByte());
        return switch(typeId) {
            case Bool -> Type.getBool();
            case Float -> Type.getFloatType();
            case Int -> Type.getIntType();
            case Any -> Type.getAny();
            case String -> Type.getString();
            case Undefined -> Type.getUndefined();
            case Unit -> Type.getUnit();
            case Array -> Type.array(readType(stream));
            case Fn -> Type.fn(
                    readType(stream),
                    readTypes(stream)
            );
            case GenericFn -> readGenericFn(stream);
            case GenericTypeParameter -> Type.typeParameter(stream.readUTF());
            case Variant -> readVariantType(stream);
        };
    }

    private static Type readGenericFn(DataInputStream stream) throws IOException {
        var genericTypes = readTypes(stream);
        var returnType = readType(stream);
        var paramTypes = readTypes(stream);
        return Type.genericFn(
                Arrays.stream(genericTypes).map(it -> (GenericTypeParameter) it).toList(),
                returnType,
                paramTypes
        );
    }

    private static Type readVariantType(DataInputStream stream) throws IOException {
        var moduleName = stream.readUTF();
        var packageName = stream.readUTF();
        var simpleName = stream.readUTF();
        var genericTypeParams = new ArrayList<GenericTypeParameter>();
        for (Type type : readTypes(stream)) {
            genericTypeParams.add((GenericTypeParameter) type);
        }

        // read concrete type map
        var count = stream.readByte();
        var concreteTypeMap = new HashMap<GenericTypeParameter, Type>();
        for (int i = 0; i < count; i++) {
            var parameter = readType(stream);
            var type = readType(stream);
            concreteTypeMap.put((GenericTypeParameter) parameter, type);
        }

        // read variant
        VariantType.Variant variant = null;
        var hasVariant = stream.readBoolean();
        if (hasVariant) {
            variant = readVariant(stream);
        }

        return new VariantType(
                moduleName,
                packageName,
                simpleName,
                genericTypeParams,
                concreteTypeMap,
                variant);
    }

    public static VariantType.Variant readVariant(DataInputStream stream) throws IOException {
        var isPublic = stream.readBoolean();
        var variantName = stream.readUTF();

        // read variant fields
        var fieldCount = stream.readByte();
        var fields = new ArrayList<VariantType.VariantField>();
        for (byte i = 0; i < fieldCount; i++) {
            var isFieldPublic = stream.readBoolean();
            var fieldName = stream.readUTF();
            var fieldType = readType(stream);
            fields.add(new VariantType.VariantField(isFieldPublic, fieldName, fieldType));
        }
        return new VariantType.Variant(
                isPublic,
                variantName,
                fields
        );
    }
}
