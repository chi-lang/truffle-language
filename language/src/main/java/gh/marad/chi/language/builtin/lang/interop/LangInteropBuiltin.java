package gh.marad.chi.language.builtin.lang.interop;

import gh.marad.chi.language.builtin.Builtin;

public abstract class LangInteropBuiltin extends Builtin {
    @Override
    public String getModuleName() {
        return "std";
    }

    @Override
    public String getPackageName() {
        return "lang.interop";
    }

}
