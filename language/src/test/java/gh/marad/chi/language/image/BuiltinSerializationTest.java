package gh.marad.chi.language.image;

import gh.marad.chi.language.ChiContext;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.nodes.ChiNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BuiltinSerializationTest {
    private static Stream<Arguments> provideBuiltinsForTest() {
        return ChiContext.builtins(System.out).stream()
                         .map(it -> Arguments.of(it, it.getClass()));
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("provideBuiltinsForTest")
    <T> void testBuiltinSerializationTest(Builtin builtin, Class<T> cls) throws Exception {
        var actual = serializeAndDeserialize(builtin);
        assertInstanceOf(cls, actual);
    }

    public ChiNode serializeAndDeserialize(ChiNode node) throws Exception {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(byteArrayOutputStream);
        var nodeWriter = new ImageWritingVisitor(outputStream);

        node.accept(nodeWriter);

        var inputStream = new DataInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        var nodeReader = new NodeReader(inputStream, System.out);

        return nodeReader.readNode();
    }
}
