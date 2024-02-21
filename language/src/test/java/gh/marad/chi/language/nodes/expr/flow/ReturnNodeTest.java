package gh.marad.chi.language.nodes.expr.flow;

import gh.marad.chi.language.TestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class ReturnNodeTest {

    @Test
    void hello() {
        var ctx = TestContext.create();
        ctx.eval("chi", """
                fn readString(s: string): string { s }
                fn eval(s: string) {}
                fn load(filePath: string): any {
                   val code = readString(filePath)
                   eval(code)
                }
                """.stripIndent());
    }

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