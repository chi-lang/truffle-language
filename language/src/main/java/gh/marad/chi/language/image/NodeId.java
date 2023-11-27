package gh.marad.chi.language.image;

public enum NodeId {
    Block,
    ReadLocalArgument,
    InvokeFunction,
    LongValue,
    StringValue,
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
