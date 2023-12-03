package gh.marad.chi.language.image;

import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeVisitor;
import gh.marad.chi.language.nodes.expr.variables.ReadLocalVariable;
import gh.marad.chi.language.nodes.expr.variables.WriteLocalVariable;
import org.graalvm.collections.EconomicSet;

public class LocalVarsCountingVisitor implements NodeVisitor {

    private final EconomicSet<String> localVarsSet = EconomicSet.create();

    @Override
    public boolean visit(Node node) {
        if (node instanceof ReadLocalVariable r) {
            localVarsSet.add(r.name);
        } else if (node instanceof WriteLocalVariable w) {
            localVarsSet.add(w.getName());
        }
        return true;
    }

    public int getCount() {
        return localVarsSet.size();
    }
}
