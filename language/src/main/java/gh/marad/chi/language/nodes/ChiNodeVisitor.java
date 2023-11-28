package gh.marad.chi.language.nodes;

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

public interface ChiNodeVisitor {
    void visitLongValue(LongValue longValue) throws Exception;
    void visitFloatValue(FloatValue floatValue) throws Exception;
    void visitStringValue(StringValue stringValue) throws Exception;
    void visitBooleanValue(BooleanValue booleanValue) throws Exception;
    void visitBuildInterpolatedString(BuildInterpolatedString buildInterpolatedString) throws Exception;
    void visitWriteLocalVariable(WriteLocalVariable writeLocalVariable) throws Exception;
    void visitReadModuleVariable(ReadModuleVariable readModuleVariable) throws Exception;
    void visitCastToLongExpr(CastToLongExpr castToLongExpr);
    void visitCastToFloat(CastToFloat castToFloat);
    void visitBlockExpr(BlockExpr blockExpr) throws Exception;
    void visitReadLocalArgument(ReadLocalArgument readLocalArgument) throws Exception;
    void visitInvokeFunction(InvokeFunction invokeFunction) throws Exception;

    void visitGetDefinedFunction(GetDefinedFunction getDefinedFunction) throws Exception;

    void visitPlusOperator(PlusOperator plusOperator) throws Exception;

}
