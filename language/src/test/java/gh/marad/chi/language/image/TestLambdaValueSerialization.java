package gh.marad.chi.language.image;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestLambdaValueSerialization {
    @Test
    void testLambdaValueSerialization() {
        // given
        try (var context = TestContext.create()) {
            context.eval("chi", """
                    package test/modimage
                    pub fn hello(): (int)->int {
                        { x:int -> x + 1 }
                    }
                    saveModule("test", "lambda.chim")
                    """.stripIndent());
        }

        try (var context = TestContext.create()) {
            context.eval("chi", """
                    loadModule("lambda.chim")
                    """.stripIndent());
            var result = context.eval("chi", """
                    import test/modimage { hello }
                    var x = hello()
                    x(4)
                    """.stripIndent());

            assertEquals(5, result.asInt());
        }

    }

}
