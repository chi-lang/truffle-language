package gh.marad.chi.language.image;

import com.oracle.truffle.api.frame.FrameDescriptor;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.expr.variables.ReadLocalArgument;
import gh.marad.chi.language.nodes.expr.variables.ReadModuleVariable;
import gh.marad.chi.language.nodes.expr.variables.WriteLocalVariable;
import gh.marad.chi.language.nodes.expr.variables.WriteLocalVariableNodeGen;
import gh.marad.chi.language.nodes.value.*;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class NodeSerializationTest {

    @Test
    void testLongValueSerialization() throws Exception {
        // given
        var expected = new LongValue(2);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof LongValue actual) {
            assertEquals(expected.value, actual.value);
        } else fail("Invalid node read!");
    }

    @Test
    void testFloatValueSerialization() throws Exception {
        // given
        var expected = new FloatValue(2.5f);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof FloatValue actual) {
            assertEquals(expected.value, actual.value);
        } else fail("Invalid node read!");
    }

    @Test
    void testStringValueSerialization() throws Exception {
        // given
        var expected = new StringValue("some string");
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof StringValue actual) {
            assertEquals(expected.value, actual.value);
        } else fail("Invalid node read!");
    }

    @Test
    void testBooleanValueSerialization() throws Exception {
        // given
        var expected = new BooleanValue(true);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof BooleanValue actual) {
            assertEquals(expected.value, actual.value);
        } else fail("Invalid node read!");
    }

    @Test
    void testInterpolatedStringSerialization() throws Exception {
        // given
        var stringValue = new StringValue("hello");
        var longValue = new LongValue(10);
        var expected = new BuildInterpolatedString(new ChiNode[]{ stringValue, longValue });
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof BuildInterpolatedString actual) {
            var parts = actual.getParts();
            assertInstanceOf(StringValue.class,  parts[0]);
            assertInstanceOf(LongValue.class, parts[1]);
        } else fail("Invalid node read!");
    }

    @Test
    void testWriteLocalVariable() throws Exception {
        // given
        var value = new LongValue(2);
        var slot = 0;
        var name = "varname";
        var expected = WriteLocalVariableNodeGen.create(value, slot, name);
        var fdBuilder = FrameDescriptor.newBuilder();
        // when
        var result = serializeAndDeserialize(expected, fdBuilder);
        var fd = fdBuilder.build();

        // then
        if (result instanceof WriteLocalVariable actual) {
            assertInstanceOf(LongValue.class, actual.getValueNode());
            assertEquals(slot, actual.getSlot());
            assertEquals(name, actual.getName());
            assertEquals(1, fd.getNumberOfSlots());
            assertEquals(name, fd.getSlotName(slot));
        } else fail("Invalid node read!");
    }

    @Test
    void testReadModuleVariable() throws Exception {
        // given
        var expected = new ReadModuleVariable(
                "module",
                "package",
                "variable"
        );

        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof ReadModuleVariable actual) {
            assertEquals(expected.moduleName, actual.moduleName);
            assertEquals(expected.packageName, actual.packageName);
            assertEquals(expected.variableName, actual.variableName);
        } else fail("Invalid node read!");
    }


    @Test
    void testReadLocalArgumentSerialization() throws Exception {
        // given
        var expected = new ReadLocalArgument(2);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof ReadLocalArgument actual) {
            assertEquals(expected.slot, actual.slot);
        } else fail("Invalid node read!");
    }

    public ChiNode serializeAndDeserialize(ChiNode node) throws Exception {
        return serializeAndDeserialize(node, FrameDescriptor.newBuilder());
    }

    public ChiNode serializeAndDeserialize(ChiNode node, FrameDescriptor.Builder fd) throws Exception {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        var outputStream = new DataOutputStream(byteArrayOutputStream);
        var nodeWriter = new ImageWritingVisitor(outputStream);

        node.accept(nodeWriter);

        var inputStream = new DataInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        var nodeReader = new NodeReader(inputStream);

        return nodeReader.withFrameDescriptor(fd, () -> {
            try {
                return nodeReader.readNode();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
