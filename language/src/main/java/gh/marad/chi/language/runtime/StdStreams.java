package gh.marad.chi.language.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import gh.marad.chi.language.ChiContext;

import java.io.PrintStream;

public class StdStreams {
    public PrintStream err;
    public PrintStream out;

    @CompilerDirectives.TruffleBoundary
    public StdStreams(ChiContext context) {
        err = new PrintStream(context.getEnv().err());
        out = new PrintStream(context.getEnv().out());
    }
}
