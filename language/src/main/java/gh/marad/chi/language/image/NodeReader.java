package gh.marad.chi.language.image;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import gh.marad.chi.core.types.FunctionType;
import gh.marad.chi.core.types.ProductType;
import gh.marad.chi.language.ChiLanguage;
import gh.marad.chi.language.EffectHandlers;
import gh.marad.chi.language.builtin.collections.*;
import gh.marad.chi.language.builtin.io.*;
import gh.marad.chi.language.builtin.lang.*;
import gh.marad.chi.language.builtin.lang.interop.LookupHostSymbolBuiltin;
import gh.marad.chi.language.builtin.lang.interop.array.HasArrayElementsBuiltin;
import gh.marad.chi.language.builtin.lang.interop.members.*;
import gh.marad.chi.language.builtin.lang.interop.values.IsNullBuiltin;
import gh.marad.chi.language.builtin.string.*;
import gh.marad.chi.language.builtin.time.MillisBuiltin;
import gh.marad.chi.language.nodes.*;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.cast.*;
import gh.marad.chi.language.nodes.expr.flow.IfExpr;
import gh.marad.chi.language.nodes.expr.flow.IsNodeGen;
import gh.marad.chi.language.nodes.expr.flow.ReturnNode;
import gh.marad.chi.language.nodes.expr.flow.ReturnUnitNode;
import gh.marad.chi.language.nodes.expr.flow.effect.HandleEffectNode;
import gh.marad.chi.language.nodes.expr.flow.effect.InvokeEffect;
import gh.marad.chi.language.nodes.expr.flow.effect.ResumeNode;
import gh.marad.chi.language.nodes.expr.flow.loop.WhileBreakNode;
import gh.marad.chi.language.nodes.expr.flow.loop.WhileContinueNode;
import gh.marad.chi.language.nodes.expr.flow.loop.WhileExprNode;
import gh.marad.chi.language.nodes.expr.operators.arithmetic.*;
import gh.marad.chi.language.nodes.expr.operators.bit.*;
import gh.marad.chi.language.nodes.expr.operators.bool.*;
import gh.marad.chi.language.nodes.expr.variables.*;
import gh.marad.chi.language.nodes.function.DefinePackageFunctionFromNodeGen;
import gh.marad.chi.language.nodes.function.GetDefinedFunction;
import gh.marad.chi.language.nodes.function.InvokeFunction;
import gh.marad.chi.language.nodes.objects.ConstructChiObject;
import gh.marad.chi.language.nodes.objects.DefineVariantTypeNode;
import gh.marad.chi.language.nodes.objects.ReadMemberNodeGen;
import gh.marad.chi.language.nodes.objects.WriteMemberNodeGen;
import gh.marad.chi.language.nodes.value.*;
import gh.marad.chi.language.runtime.ChiFunction;
import gh.marad.chi.language.runtime.TODO;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class NodeReader {
    private final DataInputStream stream;
    private final OutputStream stdOutputStream;

    public NodeReader(DataInputStream stream, OutputStream stdOutputStream) {
        this.stream = stream;
        this.stdOutputStream = stdOutputStream;
    }

    public ChiNode readNode() throws IOException {
        var nodeId = NodeId.fromId(stream.readShort());

        return switch (nodeId) {
            case UnitValue -> readUnitValue();
            case LongValue -> readLongValue();
            case FloatValue -> readFloatValue();
            case StringValue -> readStringValue();
            case BooleanValue -> readBooleanValue();
            case BuildInterpolatedString -> readBuildInterpolatedString();
            case WriteLocalVariable -> readWriteLocalVariable();
            case ReadModuleVariable -> readReadModuleVariable();
            case ReadLocalVariable -> readReadLocalVariable();
            case ReadOuterScopeVariable -> readReadOuterScopeVariable();
            case ReadOuterScopeArgument -> readReadOuterScopeArgument();
            case ReadMember -> readReadMember();
            case WriteMember -> readWriteMember();
            case Block -> readBlock();
            case ReadLocalArgument -> readLocalArgument();
            case PlusOperator -> readPlusOperator();
            case MinusOperator -> readMinusOperator();
            case MultiplyOperator -> readMultiplyOperator();
            case DivideOperator -> readDivideOperator();
            case ModuloOperator -> readModuloOperator();
            case EqualOperator -> readEqualOperator();
            case NotEqualOperator -> readNotEqualOperator();
            case LessThanOperator -> readLessThanOperator();
            case GreaterThanOperator -> readGreaterThanOperator();
            case LogicAndOperator -> readLogicAndOperator();
            case LogicOrOperator -> readLogicOrOperator();
            case BitAndOperator -> readBitAndOperator();
            case BitOrOperator -> readBitOrOperator();
            case ShlOperator -> readShlOperator();
            case ShrOperator -> readShrOperator();
            case LogicNotOperator -> readLogicNotOperator();
            case CastToLong -> readCastToLong();
            case CastToFloat -> readCastToFloat();
            case CastToString -> readCastToString();
            case IfExpr -> readIfExpr();
            case LambdaValue -> readLambdaValue();
            case WriteModuleVariable -> readWriteModuleVariable();
            case DefineModuleVariable -> readDefineModuleVariable();
            case WriteOuterVariable -> readWriteOuterVariable();
            case WriteLocalArgument -> readWriteLocalArgument();
            case InvokeFunction -> readInvokeFunction();
            case GetDefinedFunction -> readGetDefinedFunction();
            case WhileExpr -> readWhileExprNode();
            case WhileBreak -> new WhileBreakNode();
            case WhileContinue -> new WhileContinueNode();
            case IndexOperator -> readIndexOperator();
            case IndexedAssignment -> readIndexedAssignment();
            case IsExpr -> readIsExpr();
            case ConstructObject -> readConstructObject();
            case DefinePackageFunction -> readDefinePackageFunction();
            case InvokeEffect -> readInvokeEffect();
            case HandleEffect -> readHandleEffect();
            case ResumeEffect -> new ResumeNode();
            // builtins
            case ArraySizeBuiltin -> new SizeBuiltin();
            case ArrayHashBuiltin -> new ArrayHashBuiltin();
            case ArrayBuiltin -> new ArrayBuiltin();
            case ArgsBuiltin -> new ArgsBuiltin();
            case PrintBuiltin -> new PrintBuiltin(stdOutputStream);
            case PrintlnBuiltin -> new PrintlnBuiltin(stdOutputStream);
            case ReadStringBuiltin -> new ReadStringBuiltin();
            case ReadLinesBuiltin -> new ReadLinesBuiltin();
            case ClearPackageBuiltin -> new ClearPackageBuiltin();
            case LoadModuleBuiltin -> new LoadModuleBuiltin();
            case SaveModuleBuiltin -> new SaveModuleBuiltin();
            case EvalBuiltin -> new EvalBuiltin();
            case EmptyArrayBuiltin -> new EmptyArrayBuiltin();
            case LookupHostSymbolBuiltin -> new LookupHostSymbolBuiltin();
            case HasArrayElementsBuiltin -> new HasArrayElementsBuiltin();
            case IsMemberInvocableBuiltin -> new IsMemberInvocableBuiltin();
            case IsMemberModifiableBuiltin -> new IsMemberModifiable();
            case IsMemberReadableBuiltin -> new IsMemberReadableBuiltin();
            case IsMemberInternalBuiltin -> new IsMemberInternalBuiltin();
            case WriteMemberBuiltin -> new WriteMemberBuiltin();
            case ReadMemberBuiltin -> new ReadMemberBuiltin();
            case IsMemberRemovableBuiltin -> new IsMemberRemovableBuiltin();
            case IsMemberWritableBuiltin -> new IsMemberWritableBuiltin();
            case NewInstanceBuiltin -> new NewInstanceBuiltin();
            case IsMemberExistingBuiltin -> new IsMemberExistingBuiltin();
            case GetMembersBuitlin -> new GetMembersBuiltin();
            case HasMemberReadSideEffectsBuiltin -> new HasMemberReadSideEffectsBuiltin();
            case RemoveMemberBuiltin -> new RemoveMemberBuiltin();
            case HasMembersBuiltin -> new HasMembersBuiltin();
            case InvokeMemberBuiltin -> new InvokeMemberBuiltin();
            case IsMemberInsertable -> new IsMemberInsertable();
            case HasMemberWriteSideEffectsBuiltin -> new HasMemberWriteSideEffectsBuiltin();
            case IsNullBuiltin -> new IsNullBuiltin();
            case StringFromCodePointsBuiltin -> new StringFromCodePointsBuiltin();
            case ToUpperBuiltin -> new ToUpperBuiltin();
            case StringHashBuiltin -> new StringHashBuiltin();
            case StringCodePointsBuiltin -> new StringCodePointsBuiltin();
            case ToLowerBuiltin -> new ToLowerBuiltin();
            case StringLengthBuiltin -> new StringLengthBuiltin();
            case IndexOfCodePointBuiltin -> new IndexOfCodePointBuiltin();
            case SubstringBuiltin -> new SubstringBuiltin();
            case StringReplaceAllBuiltin -> new StringReplaceAllBuiltin();
            case SplitStringBuiltin -> new SplitStringBuiltin();
            case StringCodePointAtBuiltin -> new StringCodePointAtBuiltin();
            case IndexOfStringBuiltin -> new IndexOfStringBuiltin();
            case StringReplaceBuiltin -> new StringReplaceBuiltin();
            case MillisBuiltin -> new MillisBuiltin();
            case ArrayAddBuiltin -> new ArrayAddBuiltin();
            case ArrayAddAtBuiltin -> new ArrayAddAtBuiltin();
            case ArrayRemoveAtBuiltin -> new ArrayRemoveAtBuiltin();
            case ArrayRemoveBuiltin -> new ArrayRemoveBuiltin();
            case ArrayClearBuiltin -> new ArrayClearBuiltin();
            case ReturnNode -> readReturnNode();
            case ReturnUnitNode -> ReturnUnitNode.instance;
            case ExitProcessBuiltin -> ExitProcessBuiltin.instance;
        };
    }

    private ChiNode readUnitValue() {
        return new UnitValue();
    }

    public ChiNode readLongValue() throws IOException {
        return new LongValue(stream.readLong());
    }

    public ChiNode readFloatValue() throws IOException {
        return new FloatValue(stream.readFloat());
    }

    public ChiNode readStringValue() throws IOException {
        return new StringValue(stream.readUTF());
    }

    public ChiNode readBooleanValue() throws IOException {
        return new BooleanValue(stream.readBoolean());
    }

    public ChiNode readBuildInterpolatedString() throws IOException {
        var partCount = stream.readShort();
        var parts = new ChiNode[partCount];
        for(int i=0; i < partCount; i++) {
            parts[i] = readNode();
        }
        return new BuildInterpolatedString(parts);
    }

    public ChiNode readWriteLocalVariable() throws IOException {
        var slot = stream.readByte();
        var variableName = stream.readUTF();
        var valueNode = readNode();
        return WriteLocalVariableNodeGen.create(valueNode, slot, variableName);
    }

    public ChiNode readReadModuleVariable() throws IOException {
        var moduleName = stream.readUTF();
        var packageName = stream.readUTF();
        var variableNAme = stream.readUTF();
        return new ReadModuleVariable(moduleName, packageName, variableNAme);
    }

    public ChiNode readReadLocalVariable() throws IOException {
        var slot = stream.readByte();
        var name = stream.readUTF();
        return new ReadLocalVariable(name, slot);
    }

    public ChiNode readReadOuterScopeVariable() throws IOException {
        var name = stream.readUTF();
        return new ReadOuterScopeVariable(name);
    }

    public ChiNode readLocalArgument() throws IOException {
        var slot = stream.readByte();
        return new ReadLocalArgument(slot);
    }

    public ChiNode readReadOuterScopeArgument() throws IOException {
        var scopesUp = stream.readByte();
        var argIndex = stream.readByte();
        return new ReadOuterScopeArgument(scopesUp, argIndex);
    }

    public ChiNode readReadMember() throws IOException {
        var member = stream.readUTF();
        var receiver = readNode();
        return ReadMemberNodeGen.create(receiver, member);
    }

    public ChiNode readWriteMember() throws IOException {
        var member = stream.readUTF();
        var receiver = readNode();
        var value = readNode();
        return WriteMemberNodeGen.create(receiver, value, member);
    }


    public ChiNode readBlock() throws IOException {
        var bodySize = stream.readShort();
        var elements = new ChiNode[bodySize];
        for (int i = 0; i < bodySize; i++) {
            elements[i] = readNode();
        }
        return new BlockExpr(elements);
    }

    public ChiNode readPlusOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return PlusOperatorNodeGen.create(left, right);
    }

    public ChiNode readMinusOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return MinusOperatorNodeGen.create(left, right);
    }

    public ChiNode readMultiplyOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return MultiplyOperatorNodeGen.create(left, right);
    }

    public ChiNode readDivideOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return DivideOperatorNodeGen.create(left, right);
    }

    public ChiNode readModuloOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return ModuloOperatorNodeGen.create(left, right);
    }

    public ChiNode readEqualOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return EqualOperatorNodeGen.create(left, right);
    }

    public ChiNode readNotEqualOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return NotEqualOperatorNodeGen.create(left, right);
    }

    public ChiNode readLessThanOperator() throws IOException {
        var inclusive = stream.readBoolean();
        var left = readNode();
        var right = readNode();
        return LessThanOperatorNodeGen.create(inclusive, left, right);
    }

    public ChiNode readGreaterThanOperator() throws IOException {
        var inclusive = stream.readBoolean();
        var left = readNode();
        var right = readNode();
        return GreaterThanOperatorNodeGen.create(inclusive, left, right);
    }

    public ChiNode readLogicAndOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return new LogicAndOperator(left, right);
    }

    public ChiNode readLogicOrOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return new LogicOrOperator(left, right);
    }

    public ChiNode readBitAndOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return BitAndOperatorNodeGen.create(left, right);
    }

    public ChiNode readBitOrOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return BitOrOperatorNodeGen.create(left, right);
    }

    public ChiNode readShlOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return ShlOperatorNodeGen.create(left, right);
    }

    public ChiNode readShrOperator() throws IOException {
        var left = readNode();
        var right = readNode();
        return ShrOperatorNodeGen.create(left, right);
    }

    public ChiNode readLogicNotOperator() throws IOException {
        var value = readNode();
        return LogicNotOperatorNodeGen.create(value);
    }

    public ChiNode readCastToLong() throws IOException {
        var value = readNode();
        return CastToLongExprNodeGen.create(value);
    }

    public ChiNode readCastToFloat() throws IOException {
        var value = readNode();
        return CastToFloatNodeGen.create(value);
    }

    public ChiNode readCastToString() throws IOException {
        var value = readNode();
        return CastToStringNodeGen.create(value);
    }

    public ChiNode readIfExpr() throws IOException {
        var condition = readNode();
        var thenBranch = readNode();
        var elseBranch = readNode();
        return IfExpr.create(condition, thenBranch, elseBranch);
    }

    public ChiNode readWriteModuleVariable() throws IOException {
        var moduleName = stream.readUTF();
        var packageName = stream.readUTF();
        var variableName = stream.readUTF();
        var value = readNode();
        return WriteModuleVariableNodeGen.create(value, moduleName ,packageName, variableName);
    }

    public ChiNode readDefineModuleVariable() throws IOException {
        throw new TODO();
    }

    public ChiNode readWriteOuterVariable() throws IOException {
        var name = stream.readUTF();
        var value = readNode();
        return WriteOuterVariableNodeGen.create(value, name);
    }

    public ChiNode readWriteLocalArgument() throws IOException {
        int slot = stream.readByte();
        var value = readNode();
        return WriteLocalArgumentNodeGen.create(value, slot);
    }

    public ChiNode readInvokeFunction() throws IOException {
        var argCount = stream.readByte();
        var arguments = new ChiNode[argCount];
        for (byte i = 0; i < argCount; i++) {
            arguments[i] = readNode();
        }
        var function = readNode();
        return new InvokeFunction(function, arguments);
    }

    public ChiNode readGetDefinedFunction() throws IOException {
        var moduleName = stream.readUTF();
        var packageName = stream.readUTF();
        var functionName = stream.readUTF();
        var paramTypes = TypeWriter.readTypes(stream);
        return new GetDefinedFunction(moduleName, packageName, functionName, paramTypes);
    }

    public ChiNode readWhileExprNode() throws IOException {
        var condition = readNode();
        var loopNode = readNode();
        return new WhileExprNode(condition, loopNode);
    }

    public ChiNode readIndexOperator() throws  IOException {
        var variable = readNode();
        var index = readNode();
        return IndexOperatorNodeGen.create(variable, index);
    }

    public ChiNode readIndexedAssignment() throws IOException {
        var variable = readNode();
        var index = readNode();
        var value = readNode();
        return IndexedAssignmentNodeGen.create(variable, index, value);
    }

    public ChiNode readIsExpr() throws IOException {
        var typeName = stream.readUTF();
        var value = readNode();
        return IsNodeGen.create(value, typeName);
    }

    public ChiNode readConstructObject() throws IOException {
        var type = TypeWriter.readType(stream);
        var fieldCount = stream.readShort();
        var fieldNames = new String[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
            fieldNames[i] = stream.readUTF();
        }
        if (type instanceof ProductType variantType) {
            return new ConstructChiObject(variantType, fieldNames);
        } else {
            throw new TODO("Expected variant type!");
        }
    }

    private ChiNode readDefinePackageFunction() throws IOException {
        var moduleName = stream.readUTF();
        var packageName = stream.readUTF();
        var functionName = stream.readUTF();
        var type = TypeWriter.readType(stream);
        var isPublic = stream.readBoolean();
        var function = readNode();
        return DefinePackageFunctionFromNodeGen.create(
                function, moduleName, packageName, functionName, (FunctionType) type, isPublic
        );
    }

    public ChiNode readLambdaValue() throws IOException {
        var name = stream.readUTF();
        var body = readNode();
        var slotCounter = new LocalVarsCountingVisitor();
        body.accept(slotCounter);
        var fdBuilder = FrameDescriptor.newBuilder();
        fdBuilder.addSlots(slotCounter.getCount(), FrameSlotKind.Illegal);
        var language = ChiLanguage.get(body);
        var rootNode = new FnRootNode(language, fdBuilder.build(), body, name);
        return new LambdaValue(rootNode.getCallTarget());
    }

    public ChiNode readInvokeEffect() throws IOException {
        var moduleName = stream.readUTF();
        var packageName = stream.readUTF();
        var effectName = stream.readUTF();
        return new InvokeEffect(moduleName, packageName, effectName);
    }

    public ChiNode readHandleEffect() throws IOException {
        var handlersCount = stream.readShort();
        var handlers = new HashMap<EffectHandlers.Qualifier, ChiFunction>();
        for (int i = 0; i < handlersCount; i++) {
            var moduleName = stream.readUTF();
            var packageName = stream.readUTF();
            var name = stream.readUTF();
            var qualifier = new EffectHandlers.Qualifier(moduleName, packageName, name);

            var handlerName = stream.readUTF();
            var slotCounter = new LocalVarsCountingVisitor();
            var handlerBody = readNode();
            handlerBody.accept(slotCounter);
            var fdBuilder = FrameDescriptor.newBuilder();
            fdBuilder.addSlots(slotCounter.getCount(), FrameSlotKind.Illegal);
            var language = ChiLanguage.get(handlerBody);
            var rootNode = new FnRootNode(language, fdBuilder.build(), handlerBody, handlerName);
            var handlerFunction = new ChiFunction(rootNode.getCallTarget());
            handlers.put(qualifier, handlerFunction);
        }
        var blockNode = (BlockExpr) readNode();
        return new HandleEffectNode(blockNode, handlers);
    }

    private ChiNode readReturnNode() throws IOException {
        var value = readNode();
        return new ReturnNode(value);
    }

}
