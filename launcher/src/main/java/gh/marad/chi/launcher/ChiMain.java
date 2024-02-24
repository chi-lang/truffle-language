package gh.marad.chi.launcher;

import org.docopt.Docopt;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChiMain {
    private static final String doc =
            """
            Usage:
              chi repl [ARGS ...]
              chi [-m MODULE ...] [-L OPT ...] [FILE] [ARGS ...]
              
            Options:
              -m --module=MODULE       Module file or directory to load on startup
              -L --lang-opt=OPT        Language options

            Examples:
              chi -m std.chim program.chi
              chi repl preload.chi
            """.stripIndent()
            ;
    public static void main(String[] args) throws IOException {
        var opts = new Docopt(doc).parse(args);
        var options = new HashMap<String, String>();
        @SuppressWarnings("unchecked")
        var programArgs = (ArrayList<String>) opts.get("ARGS");
        String file = (String) opts.get("FILE");

        @SuppressWarnings("unchecked")
        var optsSpec = (ArrayList<String>) opts.get("--lang-opt");
        if (optsSpec != null) {
            for (String opt : optsSpec) {
                var tmp = opt.split(":", 1);
                options.put(tmp[0], tmp[1]);
            }
        }

        var context = prepareContext(programArgs.toArray(new String[]{}), options);

        @SuppressWarnings("unchecked")
        var modulesSpec = (ArrayList<String>) opts.get("--module");
        if (modulesSpec != null) {
            loadModules(modulesSpec, context);
        }

        if (file == null || "repl".equalsIgnoreCase(file)) {
            var preloadFile = programArgs.stream().findFirst()
                       .filter(it -> it.endsWith(".chi"));
            if (preloadFile.isPresent()) {
                var preloadSource = Source.newBuilder("chi", new File(preloadFile.get())).build();
                context.eval(preloadSource);
            }
            new Repl(context).loop();
        } else {
            var source = Source.newBuilder("chi", new File(file)).build();
            try {
                context.eval(source);
            } catch (PolyglotException ex) {
                if (!ex.getMessage().contains("Compilation failed")) {
                    ex.printStackTrace();
                }
            }
        }
        context.close();
    }

    private static void loadModules(ArrayList<String> modules, Context context) {
        for (String module : modules) {
            var file = new File(module);
            if (!file.exists()) {
                System.err.println("Path %s does not exist. Skipping it...");
                continue;
            }

            if (module.endsWith("chim")) {
                var path = module.replaceAll("\\\\", "\\\\\\\\");
                context.eval("chi", "loadModule(\"%s\")".formatted(path));
            } else if (file.isDirectory()) {
                var children = file.listFiles((dir, name) -> name.endsWith("chim"));
                assert children != null;
                for (File moduleFile : children) {
                    var path = moduleFile.getPath().replaceAll("\\\\", "\\\\\\\\");
                    context.eval("chi","loadModule(\"%s\")".formatted(path));
                }
            }
        }
    }

    private static Context prepareContext(String[] contextArgs, HashMap<String, String> options) {
        return Context.newBuilder("chi")
                      .in(System.in)
                      .out(System.out)
                      .err(System.err)
                      .arguments("chi", contextArgs)
                      .allowExperimentalOptions(true)
                      .allowAllAccess(true)
                      .options(options)
                      .build();
    }
}

