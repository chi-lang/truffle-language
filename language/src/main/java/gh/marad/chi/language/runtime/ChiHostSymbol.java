package gh.marad.chi.language.runtime;

public class ChiHostSymbol implements ChiValue {

    private final String symbolName;
    private final Object symbol;

    public ChiHostSymbol(String symbolName, Object symbol) {
        this.symbolName = symbolName;
        this.symbol = symbol;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public Object getSymbol() {
        return symbol;
    }
}
