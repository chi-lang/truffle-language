package gh.marad.chi.language.image;

public enum TypeId {
    Any,
    Bool,
    Float,
    Int,
    String,
    Unit,
    Fn,
    TypeVariable,
    Record,
    Sum
    ;

    public static TypeId fromId(int typeId) {
        return TypeId.values()[typeId];
    }

    public short id() {
        return (short) ordinal();
    }
}
