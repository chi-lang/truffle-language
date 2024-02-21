package gh.marad.chi.language.image;

import gh.marad.chi.core.TypeAlias;
import gh.marad.chi.core.types.*;
import gh.marad.chi.core.types.Record;
import gh.marad.chi.core.types.TypeId;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class TypeWriterTest {

    @Test
    public void testBasicTypeSerializationAndDeserialization() throws IOException {
        var types = new Type[] {
                Type.getAny(),
                Type.getBool(),
                Type.getFloat(),
                Type.getInt(),
                Type.getString(),
                Type.getUnit(),
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
        var arrayType = Type.array(Type.getInt());
        // when
        var result = serializeAndDeserializeType(arrayType);
        // then
        assertEquals(arrayType,  result);
    }

    @Test
    public void testFnTypeSerializationAndDeserialization() throws IOException {
        // given
        var fnType = Type.fn(Type.getInt(), Type.getString(), Type.getFloat());
        // when
        var result = serializeAndDeserializeType(fnType);
        // then
        assertEquals(fnType, result);
    }

    @Test
    public void testGenericFnTypeSerializationAndDeserialization() throws IOException {
        // given
        var T = new Variable("T", 0);
        var type = new Function(
                List.of(T, Type.getFloat()),
                List.of("T")
        );
        // when
        var result = serializeAndDeserializeType(type);
        // then
        if (result instanceof Function fnType) {
            assertIterableEquals(type.getTypeParams(), fnType.getTypeParams());
            assertIterableEquals(type.getTypes(), fnType.getTypes());
        } else {
            fail("Result did not deserialize to function type.");
        }
    }

    @Test
    public void testProductTypeSerialization() throws IOException {
        // given
        var type = new Record(
                new TypeId("moduleName", "packageName", "TypeName"),
                List.of(new Record.Field("i", Type.getInt())),
                List.of("T")
        );
        // when
        var result = serializeAndDeserializeType(type);
        // then
        assertEquals(type, result);
    }

    @Test
    public void testSumTypeSerialization() throws IOException {
        // given
        var T = new Variable("T", 0);
        var type = new Sum(
                new TypeId("moduleName", "packageName", "TypeName"),
                Type.getInt(),
                Type.getFloat(),
                List.of("T")
        );

        // when
        var result = serializeAndDeserializeType(type);

        // then
        assertEquals(type, result);
    }

    @Test
    public void testTypeAliasSerialization() throws IOException {
        // given
        var typeInfo = new TypeAlias(
                new TypeId("moduleName", "packageName", "TypeName"),
                Type.getInt()
        );

        // when
        var byteArrayStream = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(byteArrayStream);
        TypeWriter.writeTypeAlias(typeInfo, outputStream);
        var inputStream = new DataInputStream(new ByteArrayInputStream(byteArrayStream.toByteArray()));
        var result = TypeWriter.readTypeAlias(inputStream);

        // then
        assertEquals(typeInfo, result);
    }

    private Type serializeAndDeserializeType(Type type) throws IOException {
        var byteArrayStream = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(byteArrayStream);
        TypeWriter.writeType(type, outputStream);
        var inputStream = new DataInputStream(new ByteArrayInputStream(byteArrayStream.toByteArray()));
        return TypeWriter.readType(inputStream);
    }
}