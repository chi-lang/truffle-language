package gh.marad.chi.language.image;

import gh.marad.chi.core.Type;
import gh.marad.chi.core.VariantType;
import gh.marad.chi.language.nodes.*;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.cast.*;
import gh.marad.chi.language.nodes.expr.flow.IfExpr;
import gh.marad.chi.language.nodes.expr.flow.IsNode;
import gh.marad.chi.language.nodes.expr.flow.IsNodeGen;
import gh.marad.chi.language.nodes.expr.flow.loop.WhileBreakNode;
import gh.marad.chi.language.nodes.expr.flow.loop.WhileContinueNode;
import gh.marad.chi.language.nodes.expr.flow.loop.WhileExprNode;
import gh.marad.chi.language.nodes.expr.operators.BinaryOperator;
import gh.marad.chi.language.nodes.expr.operators.arithmetic.*;
import gh.marad.chi.language.nodes.expr.operators.bit.*;
import gh.marad.chi.language.nodes.expr.operators.bool.*;
import gh.marad.chi.language.nodes.expr.variables.*;
import gh.marad.chi.language.nodes.function.DefinePackageFunctionFromNode;
import gh.marad.chi.language.nodes.function.DefinePackageFunctionFromNodeGen;
import gh.marad.chi.language.nodes.objects.*;
import gh.marad.chi.language.nodes.value.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class NodeSerializationTest {

    @Test
    void testUnitValueSerialization() throws Exception {
        // given
        var expected = new UnitValue();
        // when
        var result = serializeAndDeserialize(expected);
        // then
        assertInstanceOf(UnitValue.class, result);
    }


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
        // when
        var result = serializeAndDeserialize(expected);

        // then
        if (result instanceof WriteLocalVariable actual) {
            assertInstanceOf(LongValue.class, actual.getValueNode());
            assertEquals(slot, actual.getSlot());
            assertEquals(name, actual.getName());
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
        // when
        var result = serializeAndDeserialize(expected);
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

    @Test
    void testNotOperator() throws Exception {
        // given
        var value = new BooleanValue(true);
        var expected = LogicNotOperatorNodeGen.create(value);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof LogicNotOperatorNodeGen actual) {
            assertInstanceOf(BooleanValue.class, actual.getValue());
        } else fail("Invalid node read!");
    }

    private static Stream<Arguments> provideCastTests() {
        var value = new StringValue("hello");
        return Stream.of(
                Arguments.of(CastToLongExprNodeGen.create(value), CastToLongExpr.class),
                Arguments.of(CastToFloatNodeGen.create(value), CastToFloat.class),
                Arguments.of(CastToStringNodeGen.create(value), CastToString.class)
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("provideCastTests")
    <T> void testCastSerialization(ChiNode castNode, Class<T> expectedClass) throws Exception {
        // when
        var result = serializeAndDeserialize(castNode);
        //then
        assertInstanceOf(expectedClass, result);
        if (result instanceof CastExpression cast) {
            assertInstanceOf(StringValue.class, cast.getValue());
        }
    }

    @Test
    void testIfElse() throws Exception {
        // given
        var expr = new BooleanValue(true);
        var thenBranch = new LongValue(1);
        var elseBranch = new UnitValue();
        var expected = IfExpr.create(expr,thenBranch,elseBranch);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof IfExpr actual) {
            assertInstanceOf(BooleanValue.class, actual.getCondition());
            assertInstanceOf(LongValue.class, actual.getThenBranch());
            assertInstanceOf(UnitValue.class, actual.getElseBranch());
        } else fail("Invalid node read!");
    }

    @Test
    void testWriteModuleVariableSerialization() throws Exception {
        // given
        var value = new LongValue(5);
        var moduleName = "moduleName";
        var packageName = "packageName";
        var variableName = "variableName";
        var type = Type.getIntType();
        var expected = WriteModuleVariableNodeGen.create(value, moduleName, packageName, variableName, type, true, false);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof WriteModuleVariable actual) {
            assertEquals(expected.getModuleName(), actual.getModuleName());
            assertEquals(expected.getPackageName(), actual.getPackageName());
            assertEquals(expected.getVariableName(), actual.getVariableName());
            assertInstanceOf(LongValue.class, actual.getValue());
            assertEquals(type, actual.getType());
            assertEquals(expected.getIsPublic(), actual.getIsPublic());
            assertEquals(expected.getIsMutable(), actual.getIsMutable());

        } else fail("Invalid node read!");
    }

    @Test
    void testWriteOuterVariableSerialization() throws Exception {
        // given
        var value = new LongValue(5);
        var expected = WriteOuterVariableNodeGen.create(value, "name");
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof WriteOuterVariable actual) {
            assertEquals(expected.getName(), actual.getName());
            assertInstanceOf(LongValue.class, actual.getValueNode());
        } else fail("Invalid node read!");
    }

    @Test
    void testWriteLocalArgumentSerialization() throws Exception {
        // given
        var value = new LongValue(5);
        var expected = WriteLocalArgumentNodeGen.create(value, 0);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof WriteLocalArgument actual) {
            assertEquals(expected.getSlot(), actual.getSlot());
            assertInstanceOf(LongValue.class, actual.getValue());
        } else fail("Invalid node read!");
    }

    @Test
    void testWhileExprNode() throws Exception {
        // given
        var condition = new BooleanValue(true);
        var loopBody = new LongValue(5);
        var expected = new WhileExprNode(condition, loopBody);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof WhileExprNode actual) {
            assertInstanceOf(BooleanValue.class, actual.getCondition());
            assertInstanceOf(LongValue.class, actual.getLoopBody());
        } else fail("Invalid node read!");
    }


    @Test
    void testWhileBreakNode() throws Exception {
        // given
        var expected = new WhileBreakNode();
        // when
        var result = serializeAndDeserialize(expected);
        // then
        assertInstanceOf(WhileBreakNode.class, result);
    }

    @Test
    void testWhileContinueNode() throws Exception {
        // given
        var expected = new WhileContinueNode();
        // when
        var result = serializeAndDeserialize(expected);
        // then
        assertInstanceOf(WhileContinueNode.class, result);
    }

    @Test
    void testIndexOperatorSerialization() throws Exception {
        // given
        var variable = new StringValue("hello world");
        var index = new LongValue(5);
        var expected = IndexOperatorNodeGen.create(variable, index);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof IndexOperatorNode actual) {
            assertInstanceOf(StringValue.class, actual.getVariable());
            assertInstanceOf(LongValue.class, actual.getIndex());
        } else fail("Invalid node read!");
    }


    @Test
    void testIndexedAssignmentSerialization() throws Exception {
        // given
        // those values doesn't make sense but in this test
        // only serialization and deserialization is crucial
        // the three child nodes need to be different types to make assertions
        // easily check for node type
        var variable = new StringValue("hello");
        var index = new LongValue(5);
        var value = new FloatValue(0.5f);
        var expected = IndexedAssignmentNodeGen.create(variable, index, value);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof IndexedAssignmentNode actual) {
            assertInstanceOf(StringValue.class, actual.getVariable());
            assertInstanceOf(LongValue.class, actual.getIndex());
            assertInstanceOf(FloatValue.class, actual.getValue());
        } else fail("Invalid node read!");
    }

    @Test
    void testIsSerialization() throws Exception {
        // given
        var value = new LongValue(5);
        var typeName = "int";
        var expected = IsNodeGen.create(value, typeName);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof IsNode actual) {
            assertInstanceOf(LongValue.class, actual.getValue());
            assertEquals(expected.getTypeName(), actual.getTypeName());
        } else fail("Invalid node read!");
    }

    @Test
    void testConstructChiObject() throws Exception {
        // given
        var type = new VariantType(
                "moduleName",
                "packageName",
                "typeName",
                List.of(), // generic type parameters
                Map.of(), // concrete parameter types
                new VariantType.Variant(
                        true, // public
                        "variantName",
                        List.of(
                                new VariantType.VariantField(
                                        true, // public
                                        "fieldName",
                                        Type.getIntType()
                                )
                        )
                )
        );
        var expected = new ConstructChiObject(type);
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof ConstructChiObject actual) {
            assertEquals(expected.type, actual.type);
        } else fail("Invalid node read!");
    }


    @Test
    void testDefinePackageFunctionSerialization() throws Exception {
        // given
        var function = new LongValue(5);
        var expected = DefinePackageFunctionFromNodeGen.create(
                function,
                "moduleName",
                "packageName",
                "functionName",
                Type.fn(Type.getIntType(), Type.getFloatType()),
                true // public
        );
        // when
        var result = serializeAndDeserialize(expected);
        // then
        if (result instanceof DefinePackageFunctionFromNode actual) {
            assertInstanceOf(LongValue.class, actual.getFunction());
            assertEquals(expected.getModuleName(), actual.getModuleName());
            assertEquals(expected.getPackageName(), actual.getPackageName());
            assertEquals(expected.getFunctionName(), actual.getFunctionName());
            assertEquals(expected.getType(), actual.getType());
            assertEquals(expected.getIsPublic(), actual.getIsPublic());
        } else fail("Invalid node read!");
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
