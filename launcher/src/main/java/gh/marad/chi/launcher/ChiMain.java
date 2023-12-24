package gh.marad.chi.launcher;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChiMain {
    public static void main(String[] args) throws IOException {
        var options = new HashMap<String, String>();
        var modulesToLoad = new ArrayList<String>();
        var programArgs = new ArrayList<String>();
        String file = null;
        for (String arg : args) {
            if (!parseOption(options, modulesToLoad, arg)) {
                if (file == null) {
                    file = arg;
                } else {
                    programArgs.add(arg);
                }
            }
        }


        var context = prepareContext(programArgs.toArray(new String[]{}), options);

        for (String module : modulesToLoad) {
            context.eval("chi", """
                        loadModule("%s")
                        """.stripIndent().formatted(module));
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

