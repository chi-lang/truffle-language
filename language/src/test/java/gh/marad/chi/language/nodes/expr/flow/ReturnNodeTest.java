package gh.marad.chi.language.nodes.expr.flow;

import gh.marad.chi.language.TestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class ReturnNodeTest {

    @Test
    void testBasicReturn() {
        var ctx = TestContext.create();
        var value = ctx.eval("chi", """
                fn a(): int { 
                    return 1
                    2
                }
                a()
                """.stripIndent());

        Assertions.assertEquals(1, value.asInt());
    }

    @Test
    void testReturnWithoutValue() {
        Assertions.assertDoesNotThrow(() -> {
            try (var ctx = TestContext.create()) {
                ctx.eval("chi", """
                    fn a() {
                        return
                        1
                    }
                    
                    a()
                    """.stripIndent());
            }
        });
    }
}