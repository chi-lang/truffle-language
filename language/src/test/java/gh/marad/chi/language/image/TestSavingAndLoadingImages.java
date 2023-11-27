package gh.marad.chi.language.image;

import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestSavingAndLoadingImages {
    @Test
    public void testImages() {
        org.graalvm.polyglot.Value result;

        // create and save module
        try (var context = createContext()) {
            context.eval("chi", """
                    package test/modimage
                    
                    pub fn hello(a: int): int { a }
                    
                    saveModule("test", "test.chim")
                    """.stripIndent());
        }

        try (var context = createContext()) {
            // load module
            context.eval("chi", """
                    loadModule("test.chim")
                    """.stripIndent());

            // import and use 'hello' function
            result = context.eval("chi", """
                    import test/modimage { hello }
                    hello(5)
                    """.stripIndent());

            assertEquals(5, result.asInt());


            // change module code (using different context to not mess with the test one)
            try(var innerContext = createContext()) {
                innerContext.eval("chi", """
                        package test/modimage

                        pub fn hello(a: int): int { a + 1 }

                        saveModule("test", "test.chim")
                        """.stripIndent());
            }

            // reload module in original context and verify that function was updated
            result = context.eval("chi", """
                    import test/modimage { hello }
                    loadModule("test.chim")
                    hello(5)
                    """.stripIndent());

            assertEquals(6, result.asInt());
        }
    }

    private Context createContext() {
        return Context.newBuilder("chi")
               .in(System.in)
               .out(System.out)
               .err(System.err)
               .allowExperimentalOptions(true)
               .allowAllAccess(true)
               .build();
    }
}
