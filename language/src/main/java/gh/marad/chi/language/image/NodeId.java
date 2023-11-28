package gh.marad.chi.language.image;

public enum NodeId {
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
