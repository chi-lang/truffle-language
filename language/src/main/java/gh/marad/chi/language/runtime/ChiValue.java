package gh.marad.chi.language.runtime;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.TruffleObject;
import gh.marad.chi.language.ChiLanguage;

public interface ChiValue extends TruffleObject {
    default boolean hasLanguage() {
        return true;
    }

    default Class<? extends TruffleLanguage<?>> getLanguage() {
        return ChiLanguage.class;
    }

    default Object toDisplayString(boolean allowSideEffects) {
        return toString();
    }
}
