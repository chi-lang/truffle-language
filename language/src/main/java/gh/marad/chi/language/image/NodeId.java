package gh.marad.chi.language.image;

public enum NodeId {
    UnitValue,
    LongValue,
    FloatValue,
    StringValue,
    BooleanValue,
    BuildInterpolatedString,
    WriteLocalVariable,
    ReadModuleVariable,
    ReadLocalVariable,
    ReadOuterScopeVariable,
    ReadLocalArgument,
    ReadOuterScopeArgument,
    ReadMember,
    WriteMember,
    Block,
    PlusOperator,
    MinusOperator,
    MultiplyOperator,
    DivideOperator,
    ModuloOperator,
    EqualOperator,
    NotEqualOperator,
    LessThanOperator,
    GreaterThanOperator,
    LogicAndOperator,
    LogicOrOperator,
    BitAndOperator,
    BitOrOperator,
    ShlOperator,
    ShrOperator,
    LogicNotOperator,
    CastToLong,
    CastToFloat,
    CastToString,
    IfExpr,
    LambdaValue,
    WriteModuleVariable,
    InvokeFunction,
    GetDefinedFunction,
    ;



    public static NodeId fromId(int nodeId) {
        return NodeId.values()[nodeId];
    }

    public short id() {
        return (short) ordinal();
    }
}
