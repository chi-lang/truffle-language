package gh.marad.chi.language.image;

import gh.marad.chi.language.TestContext;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class TestEffectSerialization {
    @Test
    void testEffectSerialization() {
        try(var context = TestContext.create()) {
            context.eval("chi", """
                    package modimage/test
                    
                    pub effect hello(name: string): string
                    
                    pub fn foo(): string {
                      handle {
                        hello("Eleven")
                      } with {
                        hello(name) -> resume("Hello $name")
                      }
                    }
                    
                    saveModule("modimage", "effect.chim")
                    """.stripIndent());
        }

        try(var context = TestContext.create()) {
            context.eval("chi", """
                    loadModule("effect.chim")
                    """.stripIndent());

            var result = context.eval("chi", """
                    import modimage/test { foo }
                    foo()
                    """.stripIndent());

            assertEquals("Hello Eleven", result.asString());
        }

    }
}
