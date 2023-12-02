package gh.marad.chi.language.image;

import gh.marad.chi.core.AnyType;
import gh.marad.chi.core.Type;

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
    GenericTypeParameter,
    Variant,
    ;

    public static TypeId fromId(int typeId) {
        return TypeId.values()[typeId];
    }

    public short id() {
        return (short) ordinal();
    }
}
