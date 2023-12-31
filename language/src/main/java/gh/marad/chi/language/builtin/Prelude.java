package gh.marad.chi.language.builtin;

import gh.marad.chi.core.namespace.PreludeImport;

import java.util.List;

public class Prelude {
    public static List<PreludeImport> imports = List.of(
            new PreludeImport("std", "collections.array", "array", null),
            new PreludeImport("std", "collections.array", "emptyArray", null),
            new PreludeImport("std", "collections.array", "arrayOf", null),
            new PreludeImport("std", "collections.set", "of", "setOf"),
            new PreludeImport("std", "collections.map", "of", "mapOf"),
            new PreludeImport("std", "collections.vector", "of", "vectorOf"),
            new PreludeImport("std", "io", "println", null),
            new PreludeImport("std", "io", "print", null),
            new PreludeImport("std", "lang", "exitProcess", null),
            new PreludeImport("std", "lang", "Option", null),
            new PreludeImport("std", "lang", "Result", null),
            new PreludeImport("std", "lang", "Pair", null),
            new PreludeImport("std", "lang.image", "saveModule", null),
            new PreludeImport("std", "lang.image", "loadModule", null)
    );
}
