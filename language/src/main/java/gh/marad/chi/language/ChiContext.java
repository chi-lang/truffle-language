package gh.marad.chi.language;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.Node;
import gh.marad.chi.core.TypeAlias;
import gh.marad.chi.core.namespace.GlobalCompilationNamespace;
import gh.marad.chi.core.namespace.Symbol;
import gh.marad.chi.language.builtin.Builtin;
import gh.marad.chi.language.builtin.Prelude;
import gh.marad.chi.language.builtin.collections.*;
import gh.marad.chi.language.builtin.io.*;
import gh.marad.chi.language.builtin.lang.*;
import gh.marad.chi.language.builtin.lang.interop.LookupHostSymbolBuiltin;
import gh.marad.chi.language.builtin.lang.interop.array.HasArrayElementsBuiltin;
import gh.marad.chi.language.builtin.lang.interop.members.*;
import gh.marad.chi.language.builtin.lang.interop.values.IsNullBuiltin;
import gh.marad.chi.language.builtin.string.*;
import gh.marad.chi.language.builtin.time.MillisBuiltin;
import gh.marad.chi.language.nodes.FnRootNode;
import gh.marad.chi.language.runtime.ChiFunction;
import gh.marad.chi.language.runtime.LexicalScope;
import gh.marad.chi.language.runtime.namespaces.Module;
import gh.marad.chi.language.runtime.namespaces.Modules;
import gh.marad.chi.language.runtime.namespaces.Package;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChiContext {
    private static final TruffleLanguage.ContextReference<ChiContext> REFERENCE = TruffleLanguage.ContextReference.create(ChiLanguage.class);

    public static ChiContext get(Node node) {
        return REFERENCE.get(node);
    }

    public final LexicalScope globalScope;
    private EffectHandlers currentEffectHandlers;

    private final ChiLanguage chiLanguage;
    private final TruffleLanguage.Env env;

    public final Modules modules = new Modules();


    public ChiContext(ChiLanguage chiLanguage, TruffleLanguage.Env env) {
        this.chiLanguage = chiLanguage;
        this.env = env;
        this.currentEffectHandlers = new EffectHandlers(null, new HashMap<>());

        var frameDescriptor = FrameDescriptor.newBuilder().build();
        this.globalScope = new LexicalScope(Truffle.getRuntime().createMaterializedFrame(new Object[0], frameDescriptor));
        installBuiltins(builtins(env.out()));
    }

    @CompilerDirectives.TruffleBoundary
    public GlobalCompilationNamespace createCompilationNamespace() {
        var ns = new GlobalCompilationNamespace(Prelude.imports);
        for (Module module : modules.listModules()) {
            for (String pkg : module.listPackages()) {
                var desc = ns.getOrCreatePackage(module.getName(), pkg);

                // define types
                for (TypeAlias typeAlias : module.listTypes(pkg)) {
                    desc.getTypes().add(typeAlias);
                }

                // define package functions
                for (Package.FunctionLookupResult function : module.listFunctions(pkg)) {
                    desc.getSymbols().add(
                            new Symbol(
                                    desc.getModuleName(),
                                    desc.getPackageName(),
                                    function.function().getExecutableName(),
                                    function.type(),
                                    function.isPublic(),
                                    true
                            )
                    );
                }

                // define package variables
                for (Package.Variable variable : module.listVariables(pkg)) {
                    desc.getSymbols().add(
                            new Symbol(
                                    desc.getModuleName(),
                                    desc.getPackageName(),
                                    variable.getName(),
                                    variable.getType(),
                                    variable.isPublic(),
                                    variable.isMutable()
                            )
                    );
                }

            }
        }
        return ns;
    }

    private void installBuiltins(List<Builtin> builtins) {
        builtins.forEach(this::installBuiltin);
    }

    private void installBuiltin(Builtin node) {
        var rootNode = new FnRootNode(chiLanguage, FrameDescriptor.newBuilder().build(), node, node.name());
        var fn = new ChiFunction(rootNode.getCallTarget());
        modules.getOrCreateModule(node.getModuleName())
               .defineFunction(node.getPackageName(), fn, node.type(), true);
    }

    public TruffleLanguage.Env getEnv() {
        return env;
    }

    public void pushHandlers(Map<EffectHandlers.Qualifier, ChiFunction> handlers) {
        var previousHandlers = currentEffectHandlers;
        currentEffectHandlers = new EffectHandlers(previousHandlers, handlers);
    }

    public void popHandlers() {
        currentEffectHandlers = currentEffectHandlers.getParent();
    }

    public ChiFunction findEffectHandlerOrNull(EffectHandlers.Qualifier qualifier) {
        return currentEffectHandlers.findEffectHandlerOrNull(qualifier);
    }

    public static List<Builtin> builtins(OutputStream outputStream) {
        return List.of(
                // lang
                new EvalBuiltin(),
                new ClearPackageBuiltin(),
                ExitProcessBuiltin.instance,
                // lang.image
                new SaveModuleBuiltin(),
                new LoadModuleBuiltin(),
                // lang.unsafe
                // lang.interop
                new LookupHostSymbolBuiltin(),
                new HasMembersBuiltin(),
                new GetMembersBuiltin(),
                new IsMemberReadableBuiltin(),
                new IsMemberModifiable(),
                new IsMemberInsertable(),
                new IsMemberRemovableBuiltin(),
                new IsMemberInvocableBuiltin(),
                new IsMemberInternalBuiltin(),
                new IsMemberWritableBuiltin(),
                new IsMemberExistingBuiltin(),
                new HasMemberReadSideEffectsBuiltin(),
                new HasMemberWriteSideEffectsBuiltin(),
                new ReadMemberBuiltin(),
                new WriteMemberBuiltin(),
                new RemoveMemberBuiltin(),
                new NewInstanceBuiltin(),
                new InvokeMemberBuiltin(),
                new IsNullBuiltin(),
                // io
                new PrintBuiltin(outputStream),
                new PrintlnBuiltin(outputStream),
                new ReadLinesBuiltin(),
                new ReadStringBuiltin(),
                new ArgsBuiltin(),
                // time
                new MillisBuiltin(),
                // collections
                new ArrayBuiltin(),
                new EmptyArrayBuiltin(),
                new SizeBuiltin(),
                new ArrayHashBuiltin(),
                new ArrayAddBuiltin(),
                new ArrayRemoveBuiltin(),
                new ArrayAddAtBuiltin(),
                new ArrayRemoveAtBuiltin(),
                new ArrayClearBuiltin(),
                new HasArrayElementsBuiltin(),
                // string
                new StringLengthBuiltin(),
                new StringCodePointAtBuiltin(),
                new SubstringBuiltin(),
                new StringHashBuiltin(),
                new StringCodePointsBuiltin(),
                new StringFromCodePointsBuiltin(),
                new IndexOfCodePointBuiltin(),
                new IndexOfStringBuiltin(),
                new ToUpperBuiltin(),
                new ToLowerBuiltin(),
                new SplitStringBuiltin(),
                new StringReplaceBuiltin(),
                new StringReplaceAllBuiltin()
        );
    }
}
