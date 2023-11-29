package gh.marad.chi.language.image;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import gh.marad.chi.language.nodes.ChiNode;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.operators.BinaryOperator;
import gh.marad.chi.language.nodes.expr.operators.BinaryOperatorWithFallback;
import gh.marad.chi.language.nodes.expr.operators.arithmetic.*;
import gh.marad.chi.language.nodes.expr.operators.bit.*;
import gh.marad.chi.language.nodes.expr.operators.bool.*;
import gh.marad.chi.language.nodes.expr.variables.*;
import gh.marad.chi.language.nodes.objects.ReadMember;
import gh.marad.chi.language.nodes.objects.ReadMemberNodeGen;
import gh.marad.chi.language.nodes.objects.WriteMember;
import gh.marad.chi.language.nodes.objects.WriteMemberNodeGen;
import gh.marad.chi.language.nodes.value.*;
import gh.marad.chi.language.runtime.namespaces.Module;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.stream.Stream;

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
    void testReadLocalVariable() throws Exception {
        // given
        var slot = 0;
        var name = "name";
        var expected = new ReadLocalVariable(name, slot);
        var fdBuilder = FrameDescriptor.newBuilder();
        fdBuilder.addSlot(FrameSlotKind.Illegal, name, null);
        // when
        var result = serializeAndDeserialize(expected, fdBuilder);
        var fd = fdBuilder.build();
        // then
        if (result instanceof ReadLocalVariable actual) {
            assertEquals(expected.slot, actual.slot);
            assertEquals(expected.name, actual.name);
        } else fail("Invalid node read!");
    }

    @Test
    void testReadOuterScopeVariable() throws Exception {
        // given
        var name = "name";
        var expected = new ReadOuterScopeVariable(name);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof ReadOuterScopeVariable actual) {
            assertEquals(expected.name, actual.name);
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

    @Test
    void testReadOuterScopeArgument() throws Exception {
        // given
        var scopesUp = 2;
        var slot = 0;
        var expected = new ReadOuterScopeArgument(scopesUp, slot);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof ReadOuterScopeArgument actual) {
            assertEquals(expected.scopesUp, actual.scopesUp);
            assertEquals(expected.argIndex, actual.argIndex);
        } else fail("Invalid node read!");
    }

    @Test
    void testReadMember() throws Exception {
        // given
        var readVariable = new ReadLocalVariable("obj", 0);
        var expected = ReadMemberNodeGen.create(readVariable, "field");
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof ReadMember actual) {
            assertEquals(expected.getMember(), actual.getMember());
            assertInstanceOf(ReadLocalVariable.class, actual.getReceiver());
        } else fail("Invalid node read!");
    }

    @Test
    void testWriteMember() throws Exception {
        // given
        var readVariable = new ReadLocalVariable("obj", 0);
        var value = new LongValue(1);
        var expected = WriteMemberNodeGen.create(readVariable, value, "field");
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof WriteMember actual) {
            assertEquals(expected.getMember(), actual.getMember());
            assertInstanceOf(LongValue.class, actual.getValue());
            assertInstanceOf(ReadLocalVariable.class, actual.getReceiver());
        } else fail("Invalid node read!");
    }

    @Test
    void testBlock() throws Exception {
        // given
        var body = new ChiNode[] {
                new LongValue(1),
                new StringValue("hello")
        };
        var expected = new BlockExpr(body);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof BlockExpr actual) {
            var actualBody = actual.getElements();
            assertInstanceOf(LongValue.class, actualBody[0]);
            assertInstanceOf(StringValue.class, actualBody[1]);
        } else fail("Invalid node read!");
    }

    private static Stream<Arguments> provideOperatorTests() {
        var left = new LongValue(1);
        var right = new StringValue("hello");
        return Stream.of(
                Arguments.of(PlusOperatorNodeGen.create(left, right), PlusOperator.class),
                Arguments.of(MinusOperatorNodeGen.create(left, right), MinusOperator.class),
                Arguments.of(MultiplyOperatorNodeGen.create(left, right), MultiplyOperator.class),
                Arguments.of(DivideOperatorNodeGen.create(left, right), DivideOperator.class),
                Arguments.of(ModuloOperatorNodeGen.create(left, right), ModuloOperator.class),
                Arguments.of(EqualOperatorNodeGen.create(left, right), EqualOperator.class),
                Arguments.of(NotEqualOperatorNodeGen.create(left, right), NotEqualOperator.class),
                Arguments.of(LessThanOperatorNodeGen.create(false, left, right), LessThanOperator.class),
                Arguments.of(GreaterThanOperatorNodeGen.create(false, left, right), GreaterThanOperator.class),
                Arguments.of(new LogicAndOperator(left, right), LogicAndOperator.class),
                Arguments.of(new LogicOrOperator(left, right), LogicOrOperator.class),
                Arguments.of(BitAndOperatorNodeGen.create(left, right), BitAndOperator.class),
                Arguments.of(BitOrOperatorNodeGen.create(left, right), BitOrOperator.class),
                Arguments.of(ShlOperatorNodeGen.create(left, right), ShlOperator.class),
                Arguments.of(ShrOperatorNodeGen.create(left, right), ShrOperator.class)
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("provideOperatorTests")
    <T> void testOperators(ChiNode expected, Class<T> operatorClass) throws Exception {
        // when
        var result = serializeAndDeserialize(expected);
        // then
        assertInstanceOf(operatorClass, result);
        if (result instanceof BinaryOperator actual) {
            assertInstanceOf(LongValue.class, actual.getLeft());
            assertInstanceOf(StringValue.class, actual.getRight());
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
