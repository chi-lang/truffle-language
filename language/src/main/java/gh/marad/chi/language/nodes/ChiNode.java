package gh.marad.chi.language.nodes;

import com.oracle.truffle.api.dsl.Introspectable;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.strings.TruffleString;
import gh.marad.chi.language.ChiLanguage;
import gh.marad.chi.language.ChiTypes;
import gh.marad.chi.language.ChiTypesGen;
import gh.marad.chi.language.runtime.ChiFunction;

@Introspectable
@TypeSystemReference(ChiTypes.class)
@NodeInfo(language = ChiLanguage.id, description = "Base for all Chi nodes.")
public abstract class ChiNode extends Node {
    private boolean hasRootTag = false;

    public void addRootTag() {
        hasRootTag = true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasTag(Class<? extends Tag> tag) {
        return hasRootTag && (tag == StandardTags.RootTag.class || tag == StandardTags.RootBodyTag.class);
    }

    public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
        var value = this.executeGeneric(frame);
        return ChiTypesGen.expectLong(value);
    }

    public float executeFloat(VirtualFrame frame) throws UnexpectedResultException {
        var value = this.executeGeneric(frame);
        return ChiTypesGen.expectFloat(value);
    }

    public boolean executeBoolean(VirtualFrame frame) throws UnexpectedResultException {
        var value = this.executeGeneric(frame);
        return ChiTypesGen.expectBoolean(value);
    }


    public TruffleString executeString(VirtualFrame frame) throws UnexpectedResultException {
        var value = this.executeGeneric(frame);
        return ChiTypesGen.expectTruffleString(value);
    }

    public ChiFunction executeFunction(VirtualFrame frame) throws UnexpectedResultException {
        var value = this.executeGeneric(frame);
        return ChiTypesGen.expectChiFunction(value);
    }

    public void executeVoid(VirtualFrame frame) {
        executeGeneric(frame);
    }

    public abstract Object executeGeneric(VirtualFrame frame);

    public abstract void accept(ChiNodeVisitor visitor) throws Exception;
}
