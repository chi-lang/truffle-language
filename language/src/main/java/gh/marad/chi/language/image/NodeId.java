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
    WriteOuterVariable,
    WriteLocalArgument,
    InvokeFunction,
    GetDefinedFunction,
    WhileExpr,
    WhileBreak,
    WhileContinue,
    IndexOperator,
    IndexedAssignment,
    IsExpr,
    ConstructObject,
    DefineVariantType,
    DefinePackageFunction,
    InvokeEffect,
    HandleEffect,
    ResumeEffect,

    // builtins
    // std/collections.array
    ArraySizeBuiltin,
    ArrayBuiltin,
    ArraySortBuiltin,
    // std/io
    ArgsBuiltin,
    PrintBuiltin,
    PrintlnBuiltin,
    ReadStringBuiltin,
    ReadLinesBuiltin,
    // std/lang
    LoadModuleBuiltin,
    SaveModuleBuiltin,
    EvalBuiltin,
    // std/lang.unsafe
    UnsafeArrayBuiltin,
    // std/lang.interop
    LookupHostSymbolBuiltin,
    // std/lang.interop.array
    HasArrayElementsBuiltin,
    // std/lang.interop.members
    IsMemberInvocableBuiltin,
    IsMemberModifiableBuiltin,
    IsMemberReadableBuiltin,
    IsMemberInternalBuiltin,
    WriteMemberBuiltin,
    ReadMemberBuiltin,
    IsMemberRemovableBuiltin,
    IsMemberWritableBuiltin,
    NewInstanceBuiltin,
    IsMemberExistingBuiltin,
    GetMembersBuitlin,
    HasMemberReadSideEffectsBuiltin,
    RemoveMemberBuiltin,
    HasMembersBuiltin,
    InvokeMemberBuiltin,
    IsMemberInsertable,
    HasMemberWriteSideEffectsBuiltin,
    // std/lang.interop.values
    IsNullBuiltin,
    // std/string
    StringFromCodePointsBuiltin,
    ToUpperBuiltin,
    StringHashBuiltin,
    StringCodePointsBuiltin,
    ToLowerBuiltin,
    StringLengthBuiltin,
    IndexOfCodePointBuiltin,
    SubstringBuiltin,
    StringReplaceAllBuiltin,
    SplitStringBuiltin,
    StringCodePointAtBuiltin,
    IndexOfStringBuiltin,
    StringReplaceBuiltin,
    // std/time
    MillisBuiltin,
    ;



    public static NodeId fromId(int nodeId) {
        return NodeId.values()[nodeId];
    }

    public short id() {
        return (short) ordinal();
    }
}
