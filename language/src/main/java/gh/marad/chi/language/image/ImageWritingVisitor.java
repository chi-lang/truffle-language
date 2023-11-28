package gh.marad.chi.language.image;

import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.cast.CastToFloat;
import gh.marad.chi.language.nodes.expr.cast.CastToLongExpr;
import gh.marad.chi.language.nodes.expr.operators.arithmetic.PlusOperator;
import gh.marad.chi.language.nodes.expr.variables.ReadLocalArgument;
import gh.marad.chi.language.nodes.expr.variables.ReadModuleVariable;
import gh.marad.chi.language.nodes.expr.variables.WriteLocalVariable;
import gh.marad.chi.language.nodes.function.GetDefinedFunction;
import gh.marad.chi.language.nodes.function.InvokeFunction;
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
    public void visitCastToLongExpr(CastToLongExpr castToLongExpr) {

        // TODO add serialization/deserialization tests for each node
        // TODO implement visitor for every node
        // TODO remove default `accept` implementation from ChiNode

    }

    @Override
    public void visitCastToFloat(CastToFloat castToFloat) {

    }

    @Override
    public void visitBlockExpr(BlockExpr blockExpr) throws IOException {
        writeNodeId(NodeId.Block);
        stream.writeShort(blockExpr.getElements().length);
    }

    @Override
    public void visitReadLocalArgument(ReadLocalArgument readLocalArgument) throws IOException {
        writeNodeId(NodeId.ReadLocalArgument);
        stream.writeByte(readLocalArgument.slot);
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
    public void visitPlusOperator(PlusOperator plusOperator) throws IOException {
        writeNodeId(NodeId.PlusOperator);
    }

    private void writeNodeId(NodeId nodeId) throws IOException {
        stream.writeShort(nodeId.id());
    }
}
