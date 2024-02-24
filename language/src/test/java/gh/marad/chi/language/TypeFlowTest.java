package gh.marad.chi.language;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TypeFlowTest {
    @Test
    void preserveTypeIdWhenCallingAFunction() {
        var ctx = TestContext.create();
        ctx.eval("chi", """
                package foo/bar
                type Point = { x: int, y: int }
                pub fn point(): Point { {x: 1, y: 2} }
                pub fn sum(p: Point): int { p.x + p.y }
                """.stripIndent());

        ctx.eval("chi", """
                import foo/bar { point }
                point().sum()
                """.stripIndent());
    }

    @Test
    void shouldPreserveTypeIdWhenDeclaringAVariable() {
        var ctx = TestContext.create();
        ctx.eval("chi", """
                package foo/bar
                type Point = { x: int, y: int }
                pub fn sum(p: Point): int { p.x + p.y }
                """.stripIndent());

        var result = ctx.eval("chi", """
                import foo/bar { Point }
                val a: Point = { x: 1, y: 2 }
                a.sum()
                """.stripIndent());

        Assertions.assertEquals(3, result.asInt());
    }

    @Test
    void foo() {
        var code = """
                fn floor(input: string): int {
                    val points = input.codePoints()
                    val open = 40
                    val close = 41
                    var i = 0
                    var fl = 0
                    while i < input.length() {
                        if points[i] == open {
                            fl += 1
                        } else {
                            fl -= 1
                        }
                        i += 1
                    }
                    fl
                }
                                
                println("(())".floor())
                """.stripIndent();

        var ctx = TestContext.create();
        var result = ctx.eval("chi", code);
    }
}
