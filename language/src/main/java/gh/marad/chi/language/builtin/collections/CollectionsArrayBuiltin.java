package gh.marad.chi.language.builtin.collections;

import gh.marad.chi.language.builtin.Builtin;

public abstract class CollectionsArrayBuiltin extends Builtin {
    @Override
    public String getModuleName() {
        return "std";
    }

    @Override
    public String getPackageName() {
        return "collections.array";
    }
}
