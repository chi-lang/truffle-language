package gh.marad.chi.language.runtime.namespaces;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;
import gh.marad.chi.core.FnType;
import gh.marad.chi.core.Type;
import gh.marad.chi.core.VariantType;
import gh.marad.chi.language.runtime.ChiFunction;

import java.util.*;

public class Package {
    private final String name;
    private final HashMap<FunctionKey, FunctionLookupResult> functions;
    private final HashMap<String, Variable> variables;
    private final HashMap<String, VariantTypeDescriptor> variantTypes;

    public Package(String name) {
        this.name = name;
        this.functions = new HashMap<>();
        this.variables = new HashMap<>();
        this.variantTypes = new HashMap<>();
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
    public Collection<Variable> listVariables() {
        return variables.values();
    }

    @CompilerDirectives.TruffleBoundary
    public void defineVariable(String name, Object value, Type type, boolean isPublic, boolean isMutable) {
        variables.put(name, new Variable(name, value, type, isPublic, isMutable));
    }

    @CompilerDirectives.TruffleBoundary
    public Object findVariableOrNull(String name) {
        var variable = variables.get(name);
        if (variable == null) return null;
        return variable.value;
    }

    @CompilerDirectives.TruffleBoundary
    public Collection<VariantTypeDescriptor> listVariantTypes() {
        return variantTypes.values();
    }

    @CompilerDirectives.TruffleBoundary
    public void defineVariantType(VariantType variantType, List<VariantType.Variant> variants) {
        variantTypes.put(variantType.getName(),
                new VariantTypeDescriptor(variantType, variants));
    }

    @CompilerDirectives.TruffleBoundary
    public VariantTypeDescriptor findVariantTypeOrNull(String name) {
        return variantTypes.get(name);
    }

    public void invalidate() {
        functions.forEach((key, lookupResult) -> {
            lookupResult.assumption.invalidate();
        });
    }


    public record FunctionKey(String name, int paramTypesHash) {
    }

    public record FunctionLookupResult(
            ChiFunction function,
            FnType type,
            boolean isPublic,
            Assumption assumption) {
    }

    public record Variable(String name, Object value, Type type, boolean isPublic, boolean isMutable) {}

    public record VariantTypeDescriptor(VariantType variantType, List<VariantType.Variant> variants) {}
}
