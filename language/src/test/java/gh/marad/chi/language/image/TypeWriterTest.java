package gh.marad.chi.language.image;

import gh.marad.chi.core.FnType;
import gh.marad.chi.core.GenericTypeParameter;
import gh.marad.chi.core.Type;
import gh.marad.chi.core.VariantType;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
class TypeWriterTest {

    @Test
    public void testBasicTypesSerializationAndDeserialization() throws IOException {
        var types = new Type[] {
                Type.getAny(),
                Type.getBool(),
                Type.getFloatType(),
                Type.getIntType(),
                Type.getString(),
                Type.getUndefined(),
                Type.getUnit(),
        };

        for (Type type : types) {
            assertEquals(
                    type,
                    serializeAndDeserializeType(type),
                    String.format("Type '%s' did not serialize correctly!", type.getName()));
        }
    }

    @Test
    public void testArrayTypeSerializationAndDeserialization() throws IOException {
        // given
        var arrayType = Type.array(Type.getIntType());
        // when
        var result = serializeAndDeserializeType(arrayType);
        // then
        assertEquals(arrayType,  result);
    }

    @Test
    public void testFnTypeSerializationAndDeserialization() throws IOException {
        // given
        var fnType = Type.fn(Type.getIntType(), Type.getString(), Type.Companion.getFloatType());
        // when
        var result = serializeAndDeserializeType(fnType);
        // then
        assertEquals(fnType, result);
    }

    @Test
    public void testGenericFnTypeSerializationAndDeserialization() throws IOException {
        // given
        var genericTypeParam = Type.typeParameter("T");
        var type = Type.genericFn(List.of(genericTypeParam), Type.getFloatType(), genericTypeParam);
        // when
        var result = serializeAndDeserializeType(type);
        // then
        if (result instanceof FnType fnType) {
            assertIterableEquals(type.getGenericTypeParameters(), fnType.getGenericTypeParameters());
            assertIterableEquals(type.getParamTypes(), fnType.getParamTypes());
            assertEquals(type.getReturnType(), fnType.getReturnType());
        } else {
            fail("Result did not deserialize to function type.");
        }
    }

    @Test
    public void testVariantTypeSerialization() throws IOException {
        // given
        var variantType = new VariantType(
                "moduleName",
                "packageName",
                "SimpleName",
                List.of(new GenericTypeParameter("T")),
                Map.of(new GenericTypeParameter("T"), Type.getIntType()),
                new VariantType.Variant(
                        true, // public
                        "VariantName",
                        List.of(
                                new VariantType.VariantField(
                                        true, // public
                                        "fieldName",
                                        Type.getIntType()
                                )
                        )
                )
        );
        // when
        var result = serializeAndDeserializeType(variantType);
        // then
        assertEquals(variantType, result);
    }

    private Type serializeAndDeserializeType(Type type) throws IOException {
        var byteArrayStream = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(byteArrayStream);
        TypeWriter.writeType(type, outputStream);
        var inputStream = new DataInputStream(new ByteArrayInputStream(byteArrayStream.toByteArray()));
        return TypeWriter.readType(inputStream);
    }
}