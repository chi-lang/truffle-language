package gh.marad.chi.language.image;

public enum ValueId {
    Bool,
    Float,
    Int,
    String,
    Array,
    Variant,
    HostObject,
    ;

    public static ValueId fromId(int typeId) {
        return ValueId.values()[typeId];
    }

    public short id() {
        return (short) ordinal();
    }
}
