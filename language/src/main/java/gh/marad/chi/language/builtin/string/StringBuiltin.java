package gh.marad.chi.language.builtin.string;

import gh.marad.chi.language.builtin.Builtin;

public abstract class StringBuiltin extends Builtin {
    @Override
    public String getModuleName() {
        return "std";
    }

    @Override
    public String getPackageName() {
        return "lang.types.string";
    }
}
