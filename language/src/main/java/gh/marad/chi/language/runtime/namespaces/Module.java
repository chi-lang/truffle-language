package gh.marad.chi.language.runtime.namespaces;

import com.oracle.truffle.api.CompilerDirectives;
import gh.marad.chi.core.TypeAlias;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.core.types.TypeScheme;
import gh.marad.chi.language.runtime.ChiFunction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class Module {
    private final String name;
    private final HashMap<String, Package> packages;

    public Module(String name) {
        this.name = name;
        this.packages = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    @CompilerDirectives.TruffleBoundary
    public boolean packageExists(String packageName) {
        return packages.containsKey(packageName);
    }

    @CompilerDirectives.TruffleBoundary
    public Set<String> listPackages() {
        return packages.keySet();
    }

    public Collection<Package.FunctionLookupResult> listFunctions(String packageName) {
        return getPackage(packageName)
                       .listFunctions();
    }

    @CompilerDirectives.TruffleBoundary
    public void defineFunction(String packageName, ChiFunction function, TypeScheme type, boolean isPublic) {
        getOrCreatePackage(packageName)
                .defineFunction(function, type, isPublic);
    }

    @CompilerDirectives.TruffleBoundary
    public void defineNamedFunction(String packageName, String name, ChiFunction function, TypeScheme type, boolean isPublic) {
        getOrCreatePackage(packageName)
                .defineNamedFunction(name, function, type, isPublic);
    }

    public Package.FunctionLookupResult findFunctionOrNull(String packageName, String functionName) {
        return getPackage(packageName)
                       .findFunctionOrNull(functionName);
    }

    public Collection<Package.Variable> listVariables(String packageName) {
        return getOrCreatePackage(packageName)
                .listVariables();
    }

    public void defineVariable(String packageName, String name, Object value, TypeScheme type, boolean isPublic, boolean isMutable) {
        getOrCreatePackage(packageName)
                .defineVariable(name, value, type, isPublic, isMutable);
    }

    public void setVariable(String packageName, String name, Object value) {
        getOrCreatePackage(packageName)
                .setVariable(name, value);
    }

    public Object findVariableFunctionOrNull(String packageName, String symbolName) {
        var pkg = getPackage(packageName);
        var variable = pkg.findVariableOrNull(symbolName);
        if (variable != null) {
            return variable;
        }
        // FIXME: proper lookup with overloaded functions
        var functionLookup = pkg.findSingleFunctionOrNull(symbolName);
        if (functionLookup != null) {
            return functionLookup.function();
        }
        return null;
    }


    public Collection<TypeAlias> listTypes(String packageName) {
        return getOrCreatePackage(packageName).listTypes();
    }

    public void defineType(String packageName, TypeAlias typeAlias) {
        getOrCreatePackage(packageName).defineType(typeAlias);
    }

    public TypeAlias findTypeOrNull(String packageName, String typeName) {
        return getOrCreatePackage(packageName).getTypeOrNull(typeName);
    }

    @CompilerDirectives.TruffleBoundary
    public void removePackage(String packageName) {
        getOrCreatePackage(packageName).invalidate();
        packages.remove(packageName);
    }


    @CompilerDirectives.TruffleBoundary
    private Package getOrCreatePackage(String packageName) {
        var pkg = packages.get(packageName);
        if (pkg != null) {
            return pkg;
        } else {
            var newPackage = new Package(packageName);
            packages.put(newPackage.getName(), newPackage);
            return newPackage;
        }
    }

    @CompilerDirectives.TruffleBoundary
    private Package getPackage(String packageName) {
        var pkg = packages.get(packageName);
        if (pkg == null) {
            CompilerDirectives.transferToInterpreter();
            throw new NoSuchPackageException(packageName);
        } else {
            return pkg;
        }
    }
}
