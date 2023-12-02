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
