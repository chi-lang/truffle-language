package gh.marad.chi.language.image;

import gh.marad.chi.language.nodes.ChiNodeVisitor;
import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.cast.CastToFloat;
import gh.marad.chi.language.nodes.expr.cast.CastToLongExpr;
import gh.marad.chi.language.nodes.expr.operators.arithmetic.PlusOperator;
import gh.marad.chi.language.nodes.expr.variables.ReadLocalArgument;
import gh.marad.chi.language.nodes.function.GetDefinedFunction;
import gh.marad.chi.language.nodes.function.InvokeFunction;
import gh.marad.chi.language.nodes.value.LongValue;
import gh.marad.chi.language.nodes.value.StringValue;

import java.io.DataOutputStream;
import java.io.IOException;

public class ImageWritingVisitor implements ChiNodeVisitor {
    private final DataOutputStream stream;

    public ImageWritingVisitor(DataOutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void visitCastToLongExpr(CastToLongExpr castToLongExpr) {

    }

    @Override
    public void visitCastToFloat(CastToFloat castToFloat) {

    }

    @Override
    public void visitBlockExpr(BlockExpr blockExpr) throws IOException {
        stream.writeShort(NodeId.Block.id());
        stream.writeShort(blockExpr.getElements().length);
    }

    @Override
    public void visitReadLocalArgument(ReadLocalArgument readLocalArgument) throws IOException {
        stream.writeShort(NodeId.ReadLocalArgument.id());
        stream.writeByte(readLocalArgument.slot);
    }

    @Override
    public void visitInvokeFunction(InvokeFunction invokeFunction) throws IOException {
        stream.writeShort(NodeId.InvokeFunction.id());
        stream.writeByte(invokeFunction.arguments.length);
    }

    @Override
    public void visitStringValue(StringValue stringValue) throws IOException {
        stream.writeShort(NodeId.StringValue.id());
        stream.writeUTF(stringValue.value.toJavaStringUncached());
    }

    @Override
    public void visitGetDefinedFunction(GetDefinedFunction getDefinedFunction) throws IOException {
        stream.writeShort(NodeId.GetDefinedFunction.id());
        stream.writeUTF(getDefinedFunction.moduleName);
        stream.writeUTF(getDefinedFunction.packageName);
        stream.writeUTF(getDefinedFunction.functionName);
        TypeWriter.writeTypes(getDefinedFunction.paramTypes, stream);
    }

    @Override
    public void visitPlusOperator(PlusOperator plusOperator) throws Exception {
        stream.writeShort(NodeId.PlusOperator.id());
    }

    @Override
    public void visitLongValue(LongValue longValue) throws Exception {
        stream.writeShort(NodeId.LongValue.id());
        stream.writeLong(longValue.value);
    }
}
