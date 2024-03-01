package gh.marad.chi.language.image;

import gh.marad.chi.language.runtime.TODO;

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
    Sum,
    Array,
    TypeScheme;

    public static TypeId fromId(int typeId) {
        if (typeId >= TypeId.values().length) {
            throw new TODO("Unknown typeId: %d".formatted(typeId));
        }
        return TypeId.values()[typeId];
    }

    public short id() {
        return (short) ordinal();
    }
}
