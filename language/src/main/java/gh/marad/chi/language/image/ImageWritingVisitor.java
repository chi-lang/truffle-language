package gh.marad.chi.language.image;

import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.cast.CastToFloat;
import gh.marad.chi.language.nodes.expr.cast.CastToLongExpr;
import gh.marad.chi.language.nodes.expr.operators.arithmetic.*;
import gh.marad.chi.language.nodes.expr.operators.bit.BitAndOperator;
import gh.marad.chi.language.nodes.expr.operators.bit.BitOrOperator;
import gh.marad.chi.language.nodes.expr.operators.bit.ShlOperator;
import gh.marad.chi.language.nodes.expr.operators.bit.ShrOperator;
import gh.marad.chi.language.nodes.expr.operators.bool.*;
import gh.marad.chi.language.nodes.expr.variables.*;
import gh.marad.chi.language.nodes.function.GetDefinedFunction;
import gh.marad.chi.language.nodes.function.InvokeFunction;
import gh.marad.chi.language.nodes.objects.ReadMember;
import gh.marad.chi.language.nodes.objects.ReadMemberNodeGen;
import gh.marad.chi.language.nodes.objects.WriteMember;
import gh.marad.chi.language.nodes.value.*;

import java.io.DataOutputStream;
import java.io.IOException;

public class ImageWritingVisitor implements ChiNodeVisitor {
    private final DataOutputStream stream;

    public ImageWritingVisitor(DataOutputStream stream) {
        this.stream = stream;
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

    // --

    @Override
    public void visitCastToLongExpr(CastToLongExpr castToLongExpr) {

        // TODO add serialization/deserialization tests for each node
        // TODO implement visitor for every node
        // TODO remove default `accept` implementation from ChiNode

    }

    @Override
    public void visitCastToFloat(CastToFloat castToFloat) {

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

    private void writeNodeId(NodeId nodeId) throws IOException {
        stream.writeShort(nodeId.id());
    }
}
