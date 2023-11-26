package gh.marad.chi.language.image;

public enum TypeId {
    Any,
    Bool,
    Float,
    Int,
    String,
    Undefined,
    Unit,
    Array,
    Fn,
    GenericFn,
    GenericTypeParameter

    ;

    public static TypeId fromId(int typeId) {
        return TypeId.values()[typeId];
    }

    public short id() {
        return (short) ordinal();
    }
}
