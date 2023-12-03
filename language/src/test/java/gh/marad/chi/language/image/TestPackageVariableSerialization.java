package gh.marad.chi.language.image;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPackageVariableSerialization {
    @Test
    void testPackageVariableSerialization() {
        try (var context = TestContext.create()) {
            context.eval("chi", """
                    package test/modimage
                    pub val variable = 5
                    saveModule("test", "variable.chim")
                    """.stripIndent());
        }

        try (var context = TestContext.create()) {
            context.eval("chi", """
                    loadModule("variable.chim")
                    """.stripIndent());
            var result = context.eval("chi", """
                    import test/modimage { variable }
                    variable
                    """.stripIndent());

            assertEquals(5, result.asInt());
        }

    }

    @Test
    void serializeDefinedTypes() {
        try (var context = TestContext.create()) {
            context.eval("chi", """
                    package modimage/test
                    
                    data Foo = pub Bar | pub Baz(pub i:int)
                    
                    saveModule("modimage", "types.chim")
                    """.stripIndent());
        }

        try (var context = TestContext.create()) {
            context.eval("chi", """
                    loadModule("types.chim")
                    """.stripIndent());

            var result = context.eval("chi", """
                    import modimage/test { Foo }
                    
                    var foo = Baz(5)
                    foo.i
                    """.stripIndent());

            assertEquals(5, result.asInt());
        }

    }

    @Test
    void complexModuleSerialization() {
        try (var context = TestContext.create()) {
            context.eval("chi", """
                    saveModule("std", "complex.chim")
                    """.stripIndent());
        }

        try (var context = TestContext.create()) {
            context.eval("chi", """
                    loadModule("complex.chim")
                    saveModule("std", "complex.chim")
                    """.stripIndent());
        }

        try (var context = TestContext.create()) {
            context.eval("chi", """
                    loadModule("complex.chim")
                    """.stripIndent());
        }
    }

}
