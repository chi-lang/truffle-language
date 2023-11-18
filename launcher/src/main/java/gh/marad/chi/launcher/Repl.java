package gh.marad.chi.launcher;

import gh.marad.chi.language.ChiLanguage;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import java.util.Scanner;

public class Repl {
    private Context context;

    public Repl(Context context) {
        this.context = context;
    }

    private String imports = "";
    private boolean shouldContinue = true;

    void loop() {
        while(true) {
            try {
                step();
                if (!shouldContinue) break;
            } catch (PolyglotException ex) {
                if (!ex.getMessage().contains("Compilation failed")) {
                    ex.printStackTrace();
                } else {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }

    private void step() {
        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if ("exit".equals(input)) {
            shouldContinue = false;
        } else if (input.startsWith("import ")) {
            recordImport(input);
        } else {
            Value result = context.eval(ChiLanguage.id, prepareSource(input));
            System.out.println(result.toString());
        }
    }

    private void recordImport(String input) {
        imports += input + "\n";
    }

    private String prepareSource(String input) {
        return (imports + "\n" + input).trim();
    }
}
