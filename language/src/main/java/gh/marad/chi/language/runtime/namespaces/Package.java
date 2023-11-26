package gh.marad.chi.language.runtime.namespaces;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;
import gh.marad.chi.core.FnType;
import gh.marad.chi.core.Type;
import gh.marad.chi.language.runtime.ChiFunction;

import java.util.*;

public class Package {
    private final String name;
    private final HashMap<FunctionKey, FunctionLookupResult> functions;
    private final HashMap<String, Object> variables;

    public Package(String name) {
        this.name = name;
        this.functions = new HashMap<>();
        this.variables = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    @CompilerDirectives.TruffleBoundary
    public Collection<FunctionLookupResult> listFunctions() {
        return functions.values();
    }

    @CompilerDirectives.TruffleBoundary
    public void defineFunction(ChiFunction function, FnType type, boolean isPublic) {
        defineNamedFunction(function.getExecutableName(), function, type, isPublic);
    }

    @CompilerDirectives.TruffleBoundary
    public void defineNamedFunction(String name, ChiFunction function, FnType type, boolean isPublic) {
        var paramTypes = type.getParamTypes().toArray(new Type[0]);
        var key = new FunctionKey(name, Objects.hash((Object[]) paramTypes));
        var oldDefinition = functions.get(key);
        if (oldDefinition != null) {
            oldDefinition.assumption.invalidate();
        }
        functions.put(key, new FunctionLookupResult(function, type, isPublic, Assumption.create("function redefined")));
    }

    @CompilerDirectives.TruffleBoundary
    public void defineVariable(String name, Object value) {
        variables.put(name, value);
    }

    @CompilerDirectives.TruffleBoundary
    public FunctionLookupResult findFunctionOrNull(String name, Type[] paramTypes) {
        var key = new FunctionKey(name, Objects.hash((Object[]) paramTypes));
        return functions.get(key);
    }

    @CompilerDirectives.TruffleBoundary
    public FunctionLookupResult findSingleFunctionOrNull(String name) {
        return functions.entrySet().stream()
                        .filter(it -> it.getKey().name.equals(name))
                        .findFirst()
                        .map(Map.Entry::getValue)
                        .orElse(null);
    }

    @CompilerDirectives.TruffleBoundary
    public Object findVariableOrNull(String name) {
        return variables.get(name);
    }

    public record FunctionKey(String name, int paramTypesHash) {
    }

    public record FunctionLookupResult(
            ChiFunction function,
            FnType type,
            boolean isPublic,
            Assumption assumption) {
    }
}
