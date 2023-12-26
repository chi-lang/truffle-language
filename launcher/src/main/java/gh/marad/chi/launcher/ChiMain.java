package gh.marad.chi.launcher;

import org.docopt.Docopt;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChiMain {
    private static final String doc =
            "Chi\n"
            + "\n"
            + "Usage:\n"
            + "  chi [-m MODULES ...] [-L OPTS] [FILE]"
            + "\n"
            + "Options:\n"
            + "  -m --modules=MODULES       Module files to load on startup\n"
            + "  -L --lang-opts=OPT         Language options\n"
            + "\n"
            + "Examples:\n"
            + "  chi -m std.chim,test.chim run.chi"
            ;
    public static void main(String[] args) throws IOException {
        var opts = new Docopt(doc).parse(args);
        var options = new HashMap<String, String>();
        var programArgs = new ArrayList<String>();
//        opts.forEach((key, value) -> {
//            System.out.println(key + " " + value);
//        });
        String file = (String) opts.get("FILE");

        var optsSpec = (String) opts.get("--lang-opts");
        if (optsSpec != null) {
            for (String opt : optsSpec.split(",")) {
                var tmp = opt.split(":", 1);
                options.put(tmp[0], tmp[1]);
            }
        }

        var context = prepareContext(programArgs.toArray(new String[]{}), options);

        @SuppressWarnings("unchecked")
        var modulesSpec = (ArrayList<String>) opts.get("--modules");

        if (modulesSpec != null) {
            for (String module : modulesSpec) {
                context.eval("chi", """
                        loadModule("%s")
                        """.stripIndent().formatted(module));
            }
        }

        if (file == null || "repl".equalsIgnoreCase(file)) {
            new Repl(context).loop();
        } else {
            var source = Source.newBuilder("chi", new File(file)).build();
            context.eval(source);
        }
        context.close();
    }

    private static Context prepareContext(String[] contextArgs, HashMap<String, String> options) {
        var context = Context.newBuilder("chi")
                              .in(System.in)
                              .out(System.out)
                              .err(System.err)
                              .arguments("chi", contextArgs)
                              .allowExperimentalOptions(true)
                              .allowAllAccess(true)
                              .options(options)
                              .build();
        return context;
    }

    private static boolean parseOption(HashMap<String, String> options, ArrayList<String> modulesToLoad, String arg) {
        System.out.println("Parsing arg: " + arg);
        if (arg.length() <= 2 || !arg.startsWith("--")) {
            return false;
        }

        String key, value;
        if (arg.contains("=")) {
            var tmp = arg.substring(2).split("=");
            key = tmp[0];
            value = tmp[1];
        } else {
            key = arg.substring(2);
            value = "true";
        }

        if ("modules".equals(key)) {
            var modules = value.split(",");
            modulesToLoad.addAll(Arrays.stream(modules).toList());
        } else {
            options.put(key, value);
        }
        return true;
    }
}

