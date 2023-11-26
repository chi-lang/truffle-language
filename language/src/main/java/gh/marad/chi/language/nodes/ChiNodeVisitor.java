package gh.marad.chi.language.nodes;

import gh.marad.chi.language.nodes.expr.BlockExpr;
import gh.marad.chi.language.nodes.expr.cast.CastToFloat;
import gh.marad.chi.language.nodes.expr.cast.CastToLongExpr;
import gh.marad.chi.language.nodes.expr.variables.ReadLocalArgument;
import gh.marad.chi.language.nodes.function.GetDefinedFunction;
import gh.marad.chi.language.nodes.function.InvokeFunction;
import gh.marad.chi.language.nodes.value.StringValue;

public interface ChiNodeVisitor {
    void visitCastToLongExpr(CastToLongExpr castToLongExpr);
    void visitCastToFloat(CastToFloat castToFloat);
    void visitBlockExpr(BlockExpr blockExpr) throws Exception;
    void visitReadLocalArgument(ReadLocalArgument readLocalArgument) throws Exception;
    void visitInvokeFunction(InvokeFunction invokeFunction) throws Exception;

    void visitStringValue(StringValue stringValue) throws Exception;

    void visitGetDefinedFunction(GetDefinedFunction getDefinedFunction) throws Exception;
}
