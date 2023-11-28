package gh.marad.chi.language.image;

public enum NodeId {
    LongValue,
    FloatValue,
    StringValue,
    BooleanValue,
    BuildInterpolatedString,
    WriteLocalVariable,
    ReadModuleVariable,
    Block,
    ReadLocalArgument,
    InvokeFunction,
    GetDefinedFunction,
    PlusOperator
    ;



    public static NodeId fromId(int nodeId) {
        return NodeId.values()[nodeId];
    }

    public short id() {
        return (short) ordinal();
    }
}
