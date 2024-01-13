package gh.marad.chi.language.image;

import gh.marad.chi.core.types.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
class TypeWriterTest {

    @Test
    public void testBasicTypesSerializationAndDeserialization() throws IOException {
        var types = new Type[] {
                Types.getAny(),
                Types.getBool(),
                Types.getFloat(),
                Types.getInt(),
                Types.getString(),
                Types.getUnit(),
        };

        for (Type type : types) {
            assertEquals(
                    type,
                    serializeAndDeserializeType(type),
                    String.format("Type '%s' did not serialize correctly!", type));
        }
    }

    @Test
    public void testArrayTypeSerializationAndDeserialization() throws IOException {
        // given
        var arrayType = Types.array(Types.getInt());
        // when
        var result = serializeAndDeserializeType(arrayType);
        // then
        assertEquals(arrayType,  result);
    }

    @Test
    public void testFnTypeSerializationAndDeserialization() throws IOException {
        // given
        var fnType = Types.fn(Types.getInt(), Types.getString(), Types.getFloat());
        // when
        var result = serializeAndDeserializeType(fnType);
        // then
        assertEquals(fnType, result);
    }

    @Test
    public void testGenericFnTypeSerializationAndDeserialization() throws IOException {
        // given
        var T = new TypeVariable("T");
        var type = new FunctionType(
                List.of(T, Types.getFloat()),
                List.of(T)
        );
        // when
        var result = serializeAndDeserializeType(type);
        // then
        if (result instanceof FunctionType fnType) {
            assertIterableEquals(type.typeSchemeVariables(), fnType.getTypeSchemeVariables());
            assertIterableEquals(type.getTypes(), fnType.getTypes());
        } else {
            fail("Result did not deserialize to function type.");
        }
    }

    @Test
    public void testProductTypeSerialization() throws IOException {
        // given
        var T = new TypeVariable("T");
        var type = new ProductType(
                "moduleName", "packageName", "TypeName",
                List.of(Types.getInt()),
                List.of(Types.getString()),
                List.of(T)
        );
        // when
        var result = serializeAndDeserializeType(type);
        // then
        assertEquals(type, result);
    }

    @Test
    public void testSumTypeSerialization() throws IOException {
        fail();
    }

    @Test
    public void testTypeInfoSerialization() throws IOException {
        fail();
    }

    private Type serializeAndDeserializeType(Type type) throws IOException {
        var byteArrayStream = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(byteArrayStream);
        TypeWriter.writeType(type, outputStream);
        var inputStream = new DataInputStream(new ByteArrayInputStream(byteArrayStream.toByteArray()));
        return TypeWriter.readType(inputStream);
    }
}