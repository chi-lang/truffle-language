package gh.marad.chi.language.image;

import gh.marad.chi.core.VariantType;
import gh.marad.chi.language.EffectHandlers;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.FnRootNode;
import gh.marad.chi.language.nodes.IndexOperatorNode;
import gh.marad.chi.language.nodes.IndexedAssignmentNode;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.cast.CastToFloat;
import gh.marad.chi.language.nodes.expr.cast.CastToLongExpr;
import gh.marad.chi.language.nodes.expr.cast.CastToString;
import gh.marad.chi.language.nodes.expr.flow.IfExpr;
import gh.marad.chi.language.nodes.expr.flow.IsNode;
import gh.marad.chi.language.nodes.expr.flow.ReturnNode;
import gh.marad.chi.language.nodes.expr.flow.ReturnUnitNode;
import gh.marad.chi.language.nodes.expr.flow.effect.HandleEffectNode;
import gh.marad.chi.language.nodes.expr.flow.effect.InvokeEffect;
import gh.marad.chi.language.nodes.expr.flow.effect.ResumeNode;
import gh.marad.chi.language.nodes.expr.flow.loop.WhileBreakNode;
import gh.marad.chi.language.nodes.expr.flow.loop.WhileContinueNode;
import gh.marad.chi.language.nodes.expr.flow.loop.WhileExprNode;
import gh.marad.chi.language.nodes.expr.operators.arithmetic.*;
import gh.marad.chi.language.nodes.expr.operators.bit.BitAndOperator;
import gh.marad.chi.language.nodes.expr.operators.bit.BitOrOperator;
import gh.marad.chi.language.nodes.expr.operators.bit.ShlOperator;
import gh.marad.chi.language.nodes.expr.operators.bit.ShrOperator;
import gh.marad.chi.language.nodes.expr.operators.bool.*;
import gh.marad.chi.language.nodes.expr.variables.*;
import gh.marad.chi.language.nodes.function.DefinePackageFunction;
import gh.marad.chi.language.nodes.function.DefinePackageFunctionFromNode;
import gh.marad.chi.language.nodes.function.GetDefinedFunction;
import gh.marad.chi.language.nodes.function.InvokeFunction;
import gh.marad.chi.language.nodes.objects.ConstructChiObject;
import gh.marad.chi.language.nodes.objects.DefineVariantTypeNode;
import gh.marad.chi.language.nodes.objects.ReadMember;
import gh.marad.chi.language.nodes.objects.WriteMember;
import gh.marad.chi.language.nodes.value.*;
import gh.marad.chi.language.runtime.TODO;

import java.io.DataOutputStream;
import java.io.IOException;

public class ImageWritingVisitor implements ChiNodeVisitor {
    private final DataOutputStream stream;

    public ImageWritingVisitor(DataOutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void visitUnitValue(UnitValue unitValue) throws IOException {
        writeNodeId(NodeId.UnitValue);
    }

    @Override
    public void visitLongValue(LongValue longValue) throws IOException {
        writeNodeId(NodeId.LongValue);
        stream.writeLong(longValue.value);
    }

    @Override
    public void visitFloatValue(FloatValue floatValue) throws IOException {
        writeNodeId(NodeId.FloatValue);
        stream.writeFloat(floatValue.value);
    }

    @Override
    public void visitStringValue(StringValue stringValue) throws IOException {
        writeNodeId(NodeId.StringValue);
        stream.writeUTF(stringValue.value.toJavaStringUncached());
    }

    @Override
    public void visitBooleanValue(BooleanValue booleanValue) throws IOException {
        writeNodeId(NodeId.BooleanValue);
        stream.writeBoolean(booleanValue.value);
    }

    @Override
    public void visitBuildInterpolatedString(BuildInterpolatedString buildInterpolatedString) throws IOException {
        writeNodeId(NodeId.BuildInterpolatedString);
        stream.writeShort(buildInterpolatedString.getParts().length);
    }

    @Override
    public void visitWriteLocalVariable(WriteLocalVariable writeLocalVariable) throws IOException {
        writeNodeId(NodeId.WriteLocalVariable);
        stream.writeByte(writeLocalVariable.getSlot());
        stream.writeUTF(writeLocalVariable.getName());
    }

    @Override
    public void visitReadModuleVariable(ReadModuleVariable readModuleVariable) throws IOException {
        writeNodeId(NodeId.ReadModuleVariable);
        stream.writeUTF(readModuleVariable.moduleName);
        stream.writeUTF(readModuleVariable.packageName);
        stream.writeUTF(readModuleVariable.variableName);
    }

    @Override
    public void visitReadLocalVariable(ReadLocalVariable readLocalVariable) throws IOException {
        writeNodeId(NodeId.ReadLocalVariable);
        stream.writeByte(readLocalVariable.slot);
        stream.writeUTF(readLocalVariable.name);
    }

    @Override
    public void visitReadOuterScopeVariable(ReadOuterScopeVariable readOuterScopeVariable) throws IOException {
        writeNodeId(NodeId.ReadOuterScopeVariable);
        stream.writeUTF(readOuterScopeVariable.name);
    }

    @Override
    public void visitReadLocalArgument(ReadLocalArgument readLocalArgument) throws IOException {
        writeNodeId(NodeId.ReadLocalArgument);
        stream.writeByte(readLocalArgument.slot);
    }

    @Override
    public void visitReadOuterScopeArgument(ReadOuterScopeArgument readOuterScopeArgument) throws IOException {
        writeNodeId(NodeId.ReadOuterScopeArgument);
        stream.writeByte(readOuterScopeArgument.scopesUp);
        stream.writeByte(readOuterScopeArgument.argIndex);
    }

    @Override
    public void visitReadMember(ReadMember readMember) throws IOException {
        writeNodeId(NodeId.ReadMember);
        stream.writeUTF(readMember.getMember());
    }

    @Override
    public void visitWriteMember(WriteMember writeMember) throws IOException {
        writeNodeId(NodeId.WriteMember);
        stream.writeUTF(writeMember.getMember());
    }

    @Override
    public void visitBlockExpr(BlockExpr blockExpr) throws IOException {
        writeNodeId(NodeId.Block);
        stream.writeShort(blockExpr.getElements().length);
    }

    @Override
    public void visitPlusOperator(PlusOperator plusOperator) throws IOException {
        writeNodeId(NodeId.PlusOperator);
    }

    @Override
    public void visitMinusOperator(MinusOperator minusOperator) throws IOException {
        writeNodeId(NodeId.MinusOperator);
    }

    @Override
    public void visitMultiplyOperator(MultiplyOperator multiplyOperator) throws IOException {
        writeNodeId(NodeId.MultiplyOperator);
    }

    @Override
    public void visitDivideOperator(DivideOperator divideOperator) throws IOException {
        writeNodeId(NodeId.DivideOperator);
    }

    @Override
    public void visitModuloOperator(ModuloOperator moduloOperator) throws IOException {
        writeNodeId(NodeId.ModuloOperator);
    }

    @Override
    public void visitEqualOperator(EqualOperator equalOperator) throws IOException {
        writeNodeId(NodeId.EqualOperator);
    }

    @Override
    public void visitNotEqual(NotEqualOperator notEqualOperator) throws IOException {
        writeNodeId(NodeId.NotEqualOperator);
    }

    @Override
    public void visitLessThanOperator(LessThanOperator lessThanOperator) throws IOException {
        writeNodeId(NodeId.LessThanOperator);
        stream.writeBoolean(lessThanOperator.inclusive);
    }

    @Override
    public void visitGreaterThanOperator(GreaterThanOperator greaterThanOperator) throws IOException {
        writeNodeId(NodeId.GreaterThanOperator);
        stream.writeBoolean(greaterThanOperator.inclusive);
    }

    @Override
    public void visitLogicAndOperator(LogicAndOperator logicAndOperator) throws IOException {
        writeNodeId(NodeId.LogicAndOperator);
    }

    @Override
    public void visitLogicOrOperator(LogicOrOperator logicOrOperator) throws IOException {
        writeNodeId(NodeId.LogicOrOperator);
    }

    @Override
    public void visitBitAndOperator(BitAndOperator bitAndOperator) throws IOException {
        writeNodeId(NodeId.BitAndOperator);
    }

    @Override
    public void visitBitOrOperator(BitOrOperator bitOrOperator) throws IOException {
        writeNodeId(NodeId.BitOrOperator);
    }

    @Override
    public void visitShlOperator(ShlOperator shlOperator) throws IOException {
        writeNodeId(NodeId.ShlOperator);
    }

    @Override
    public void visitShrOperator(ShrOperator shrOperator) throws IOException {
        writeNodeId(NodeId.ShrOperator);
    }

    @Override
    public void visitLogicNotOperator(LogicNotOperator logicNotOperator) throws IOException {
        writeNodeId(NodeId.LogicNotOperator);
    }

    @Override
    public void visitCastToLongExpr(CastToLongExpr castToLongExpr) throws IOException {
        writeNodeId(NodeId.CastToLong);
    }

    @Override
    public void visitCastToFloat(CastToFloat castToFloat) throws IOException {
        writeNodeId(NodeId.CastToFloat);
    }

    @Override
    public void visitCastToString(CastToString castToString) throws IOException {
        writeNodeId(NodeId.CastToString);
    }

    @Override
    public void visitIfExpr(IfExpr ifExpr) throws Exception {
        writeNodeId(NodeId.IfExpr);
    }

    @Override
    public void visitLambdaValue(LambdaValue lambdaValue) throws Exception {
        writeNodeId(NodeId.LambdaValue);
        var rootNode = lambdaValue.callTarget.getRootNode();
        if (rootNode instanceof FnRootNode fnRootNode) {
            stream.writeUTF(fnRootNode.getName());
            fnRootNode.accept(this);
        } else {
            throw new TODO("Cannot serialize foreign language functions");
        }
    }

    @Override
    public void visitWriteModuleVariable(WriteModuleVariable writeModuleVariable) throws IOException {
        writeNodeId(NodeId.WriteModuleVariable);
        stream.writeUTF(writeModuleVariable.getModuleName());
        stream.writeUTF(writeModuleVariable.getPackageName());
        stream.writeUTF(writeModuleVariable.getVariableName());
        TypeWriter.writeType(writeModuleVariable.getType(), stream);
        stream.writeBoolean(writeModuleVariable.getIsPublic());
        stream.writeBoolean(writeModuleVariable.getIsMutable());
    }

    @Override
    public void visitWriteOuterVariable(WriteOuterVariable writeOuterVariable) throws IOException {
        writeNodeId(NodeId.WriteOuterVariable);
        stream.writeUTF(writeOuterVariable.getName());
    }

    @Override
    public void visitWriteLocalArgument(WriteLocalArgument writeLocalArgument) throws IOException {
        writeNodeId(NodeId.WriteLocalArgument);
        stream.writeByte(writeLocalArgument.getSlot());
    }

    @Override
    public void visitInvokeFunction(InvokeFunction invokeFunction) throws IOException {
        writeNodeId(NodeId.InvokeFunction);
        stream.writeByte(invokeFunction.arguments.length);
    }

    @Override
    public void visitGetDefinedFunction(GetDefinedFunction getDefinedFunction) throws IOException {
        writeNodeId(NodeId.GetDefinedFunction);
        stream.writeUTF(getDefinedFunction.moduleName);
        stream.writeUTF(getDefinedFunction.packageName);
        stream.writeUTF(getDefinedFunction.functionName);
        TypeWriter.writeTypes(getDefinedFunction.paramTypes, stream);
    }

    @Override
    public void visitWhileExprNode(WhileExprNode whileExprNode) throws IOException {
        writeNodeId(NodeId.WhileExpr);
    }

    @Override
    public void visitWhileBreakNode(WhileBreakNode whileBreakNode) throws IOException {
        writeNodeId(NodeId.WhileBreak);
    }

    @Override
    public void visitWhileContinueNode(WhileContinueNode whileContinueNode) throws IOException {
        writeNodeId(NodeId.WhileContinue);
    }

    @Override
    public void visitIndexOperator(IndexOperatorNode indexOperatorNode) throws IOException {
        writeNodeId(NodeId.IndexOperator);
    }

    @Override
    public void visitIndexedAssignmentNode(IndexedAssignmentNode indexedAssignmentNode) throws IOException {
        writeNodeId(NodeId.IndexedAssignment);
    }

    @Override
    public void visitIs(IsNode isNode) throws IOException {
        writeNodeId(NodeId.IsExpr);
        stream.writeUTF(isNode.getTypeName());
    }

    @Override
    public void visitConstructChiObject(ConstructChiObject constructChiObject) throws IOException {
        writeNodeId(NodeId.ConstructObject);
        TypeWriter.writeType(constructChiObject.type, stream);
    }

    @Override
    public void visitDefineVariantTypeNode(DefineVariantTypeNode defineVariantTypeNode) throws Exception {
        writeNodeId(NodeId.DefineVariantType);
        TypeWriter.writeType(defineVariantTypeNode.type, stream);
        stream.writeShort(defineVariantTypeNode.variants.size());
        for (VariantType.Variant variant : defineVariantTypeNode.variants) {
            TypeWriter.writeVariant(variant, stream);
        }
    }

    @Override
    public void visitDefinePackageFunction(DefinePackageFunctionFromNode node) throws IOException {
        writeNodeId(NodeId.DefinePackageFunction);
        stream.writeUTF(node.getModuleName());
        stream.writeUTF(node.getPackageName());
        stream.writeUTF(node.getFunctionName());
        TypeWriter.writeType(node.getType(), stream);
        stream.writeBoolean(node.getIsPublic());
    }

    @Override
    public void visitDefinePackageFunction(DefinePackageFunction definePackageFunction) {
        throw new TODO("Package functions should be serialized by ModuleWriter!");
    }

    @Override
    public void visitInvokeEffect(InvokeEffect invokeEffect) throws IOException {
        writeNodeId(NodeId.InvokeEffect);
        stream.writeUTF(invokeEffect.moduleName);
        stream.writeUTF(invokeEffect.packageName);
        stream.writeUTF(invokeEffect.effectName);
    }

    @Override
    public void visitHandleEffect(HandleEffectNode handle) throws Exception {
        writeNodeId(NodeId.HandleEffect);
        stream.writeShort(handle.handlers.size());
        for (EffectHandlers.Qualifier qualifier : handle.handlers.keySet()) {
            stream.writeUTF(qualifier.module());
            stream.writeUTF(qualifier.pkg());
            stream.writeUTF(qualifier.name());

            var function = handle.handlers.get(qualifier);
            var rootNode = function.getCallTarget().getRootNode();
            if (rootNode instanceof FnRootNode fnRootNode) {
                stream.writeUTF(fnRootNode.getName());
                fnRootNode.accept(this);
            } else {
                throw new TODO("Cannot serialize foreign language functions");
            }
        }
    }

    @Override
    public void visitResumeNode(ResumeNode resumeNode) throws Exception {
        writeNodeId(NodeId.ResumeEffect);
    }

    @Override
    public void visitBuiltin(Builtin builtin) throws Exception {
        writeNodeId(builtin.getNodeId());
    }

    @Override
    public void visitReturnNode(ReturnNode returnNode) throws Exception {
        writeNodeId(NodeId.ReturnNode);
    }

    @Override
    public void visitReturnUnitNode(ReturnUnitNode returnUnitNode) throws Exception {
        writeNodeId(NodeId.ReturnUnitNode);
    }

    private void writeNodeId(NodeId nodeId) throws IOException {
        stream.writeShort(nodeId.id());
    }

}
