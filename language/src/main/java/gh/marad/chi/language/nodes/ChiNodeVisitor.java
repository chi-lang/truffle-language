package gh.marad.chi.language.nodes;

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
import gh.marad.chi.language.nodes.objects.WriteMember;
import gh.marad.chi.language.nodes.value.*;

public interface ChiNodeVisitor {
    void visitLongValue(LongValue longValue) throws Exception;
    void visitFloatValue(FloatValue floatValue) throws Exception;
    void visitStringValue(StringValue stringValue) throws Exception;
    void visitBooleanValue(BooleanValue booleanValue) throws Exception;
    void visitBuildInterpolatedString(BuildInterpolatedString buildInterpolatedString) throws Exception;
    void visitWriteLocalVariable(WriteLocalVariable writeLocalVariable) throws Exception;
    void visitReadModuleVariable(ReadModuleVariable readModuleVariable) throws Exception;
    void visitReadLocalVariable(ReadLocalVariable readLocalVariable) throws Exception;
    void visitReadOuterScopeVariable(ReadOuterScopeVariable readOuterScopeVariable) throws Exception;
    void visitReadLocalArgument(ReadLocalArgument readLocalArgument) throws Exception;
    void visitReadOuterScopeArgument(ReadOuterScopeArgument readOuterScopeArgument) throws Exception;
    void visitReadMember(ReadMember readMember) throws Exception;
    void visitWriteMember(WriteMember writeMember) throws Exception;
    void visitBlockExpr(BlockExpr blockExpr) throws Exception;
    void visitPlusOperator(PlusOperator plusOperator) throws Exception;
    void visitMinusOperator(MinusOperator minusOperator) throws Exception;
    void visitMultiplyOperator(MultiplyOperator multiplyOperator) throws Exception;
    void visitDivideOperator(DivideOperator divideOperator) throws Exception;
    void visitModuloOperator(ModuloOperator moduloOperator) throws Exception;
    void visitEqualOperator(EqualOperator equalOperator) throws Exception;
    void visitNotEqual(NotEqualOperator notEqualOperator) throws Exception;
    void visitLessThanOperator(LessThanOperator lessThanOperator) throws Exception;
    void visitGreaterThanOperator(GreaterThanOperator greaterThanOperator) throws Exception;
    void visitLogicAndOperator(LogicAndOperator logicAndOperator) throws Exception;
    void visitLogicOrOperator(LogicOrOperator logicOrOperator) throws Exception;

    void visitBitAndOperator(BitAndOperator bitAndOperator) throws Exception;
    void visitBitOrOperator(BitOrOperator bitOrOperator) throws Exception;
    void visitShlOperator(ShlOperator shlOperator) throws Exception;
    void visitShrOperator(ShrOperator shrOperator) throws Exception;

    // ---
    void visitCastToLongExpr(CastToLongExpr castToLongExpr);
    void visitCastToFloat(CastToFloat castToFloat);
    void visitInvokeFunction(InvokeFunction invokeFunction) throws Exception;

    void visitGetDefinedFunction(GetDefinedFunction getDefinedFunction) throws Exception;

}
