package gh.marad.chi.launcher;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChiMain {
    public static void main(String[] args) throws IOException {
        if(args.length == 0 || args[0].equals("repl")) {
            String[] contextArgs = null;
            if (args.length == 0) {
                contextArgs = new String[] {};
            } else {
                contextArgs = Arrays.copyOfRange(args, 1, args.length);
            }
            var options = new HashMap<String, String>();
            new Repl(prepareContext(contextArgs, options))
                    .loop();
        } else {
            var options = new HashMap<String, String>();
            var programArgs = new ArrayList<String>();
            String file = null;
            for (String arg : args) {
                if (!parseOption(options, arg)) {
                    if (file == null) {
                        file = arg;
                    } else {
                        programArgs.add(arg);
                    }
                }
            }

            var source = Source.newBuilder("chi", new File(file)).build();
            var context = prepareContext(programArgs.toArray(new String[]{}), options);

            context.eval(source);
            context.close();
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

    private static boolean parseOption(HashMap<String, String> options, String arg) {
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
        options.put(key, value);
        return true;
    }
}

