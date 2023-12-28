package gh.marad.chi.language;

import org.graalvm.polyglot.Context;

public class TestContext {
    public static Context create() {
        return Context.newBuilder("chi")
                      .in(System.in)
                      .out(System.out)
                      .err(System.err)
                      .allowExperimentalOptions(true)
                      .allowAllAccess(true)
                      .option("log.file", "truffle.log")
                      .build();
    }
}
