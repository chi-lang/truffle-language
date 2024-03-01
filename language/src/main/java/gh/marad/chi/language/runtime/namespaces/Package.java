package gh.marad.chi.language.runtime.namespaces;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.CompilerDirectives;
import gh.marad.chi.core.TypeAlias;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.core.types.TypeScheme;
import gh.marad.chi.language.runtime.ChiFunction;

import java.util.*;

public class Package {
    private final String name;
    private final HashMap<String, FunctionLookupResult> functions;
    private final HashMap<String, Variable> variables;
    private final HashMap<String, TypeAlias> types;

    public Package(String name) {
        this.name = name;
        this.functions = new HashMap<>();
        this.variables = new HashMap<>();
        this.types = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    @CompilerDirectives.TruffleBoundary
    public Collection<FunctionLookupResult> listFunctions() {
        return functions.values();
    }

    @CompilerDirectives.TruffleBoundary
    public void defineFunction(ChiFunction function, TypeScheme type, boolean isPublic) {
        defineNamedFunction(function.getExecutableName(), function, type, isPublic);
    }

    @CompilerDirectives.TruffleBoundary
    public void defineNamedFunction(String name, ChiFunction function, TypeScheme type, boolean isPublic) {
        var oldDefinition = functions.get(name);
        if (oldDefinition != null) {
            oldDefinition.assumption.invalidate();
        }
        functions.put(name, new FunctionLookupResult(function, type, isPublic, Assumption.create("function redefined")));
    }

    @CompilerDirectives.TruffleBoundary
    public FunctionLookupResult findFunctionOrNull(String name) {
        return functions.get(name);
    }

    @CompilerDirectives.TruffleBoundary
    public FunctionLookupResult findSingleFunctionOrNull(String name) {
        return functions.entrySet().stream()
                        .filter(it -> it.getKey().equals(name))
                        .findFirst()
                        .map(Map.Entry::getValue)
                        .orElse(null);
    }

    @CompilerDirectives.TruffleBoundary
    public Collection<Variable> listVariables() {
        return variables.values();
    }

    @CompilerDirectives.TruffleBoundary
    public void defineVariable(String name, Object value, TypeScheme type, boolean isPublic, boolean isMutable) {
        variables.put(name, new Variable(name, value, type, isPublic, isMutable));
    }

    @CompilerDirectives.TruffleBoundary
    public void setVariable(String name, Object value) {
        var variable = variables.get(name);
        if (variable != null) {
            variable.value = value;
        }
    }

    @CompilerDirectives.TruffleBoundary
    public Object findVariableOrNull(String name) {
        var variable = variables.get(name);
        if (variable == null) return null;
        return variable.value;
    }

    @CompilerDirectives.TruffleBoundary
    public void defineType(TypeAlias typeAlias) {
        types.put(typeAlias.getTypeId().getName(), typeAlias);
    }

    @CompilerDirectives.TruffleBoundary
    public TypeAlias getTypeOrNull(String name) {
        return types.get(name);
    }

    @CompilerDirectives.TruffleBoundary
    public Collection<TypeAlias> listTypes() {
        return types.values();
    }

    public void invalidate() {
        functions.forEach((key, lookupResult) -> lookupResult.assumption.invalidate());
    }


    public record FunctionLookupResult(
            ChiFunction function,
            TypeScheme type,
            boolean isPublic,
            Assumption assumption) {
    }

//    public record Variable(String name, Object value, Type type, boolean isPublic, boolean isMutable) {
//    }

    public static final class Variable {
        final String name;
        Object value;
        TypeScheme type;
        boolean isPublic;
        boolean isMutable;

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        public TypeScheme getType() {
            return type;
        }

        public boolean isPublic() {
            return isPublic;
        }

        public boolean isMutable() {
            return isMutable;
        }

        public Variable(String name, Object value, TypeScheme type, boolean isPublic, boolean isMutable) {
            this.name = name;
            this.value = value;
            this.type = type;
            this.isPublic = isPublic;
            this.isMutable = isMutable;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            Variable variable = (Variable) object;
            return isPublic == variable.isPublic && isMutable == variable.isMutable && Objects.equals(name, variable.name) && Objects.equals(value, variable.value) && Objects.equals(type, variable.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value, type, isPublic, isMutable);
        }
    }
}
