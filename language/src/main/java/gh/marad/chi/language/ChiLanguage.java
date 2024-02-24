package gh.marad.chi.language;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.object.Shape;
import gh.marad.chi.core.TypeAlias;
import gh.marad.chi.core.analyzer.Level;
import gh.marad.chi.core.analyzer.Message;
import gh.marad.chi.core.compiler.Compiler;
import gh.marad.chi.core.types.Type;
import gh.marad.chi.language.compilation.CompilationFailed;
import gh.marad.chi.language.runtime.ChiObject;

import java.io.PrintWriter;

@TruffleLanguage.Registration(
        id = ChiLanguage.id,
        name = "Chi",
        defaultMimeType = ChiLanguage.mimeType,
        characterMimeTypes = ChiLanguage.mimeType,
        contextPolicy = TruffleLanguage.ContextPolicy.SHARED
)
@ProvidedTags({StandardTags.RootTag.class, StandardTags.ExpressionTag.class, StandardTags.RootBodyTag.class,
        StandardTags.ReadVariableTag.class, StandardTags.WriteVariableTag.class})
public class ChiLanguage extends TruffleLanguage<ChiContext> {
    public static final String id = "chi";
    public static final String mimeType = "application/x-chi";
    private static final LanguageReference<ChiLanguage> REFERENCE = LanguageReference.create(ChiLanguage.class);

    private static final Shape initialObjectShape = Shape.newBuilder().build();

    public static ChiObject createObject(Type type, Env env) {
        return new ChiObject(type, initialObjectShape, env);
    }

    public static ChiLanguage get(Node node) {
        return REFERENCE.get(node);
    }

    @Override
    protected ChiContext createContext(Env env) {
        return new ChiContext(this, env);
    }

    @Override
    protected CallTarget parse(ParsingRequest request) {
        var source = request.getSource();
        var sourceString = source.getCharacters().toString();
        return compile(sourceString);
    }

    @CompilerDirectives.TruffleBoundary
    public CallTarget compile(String sourceString) {
        var context = ChiContext.get(null);
        var compiled = Compiler.compile(sourceString, context.createCompilationNamespace());

        if (compiled.hasErrors()) {
            CompilerDirectives.transferToInterpreter();
            for (Message message : compiled.getMessages()) {
                if (message.getLevel() == Level.ERROR) {
                    var msgStr = Compiler.formatCompilationMessage(sourceString, message);
                    var err = new PrintWriter(context.getEnv().err());
                    err.println(msgStr);
                    err.flush();
                }
            }
            throw new CompilationFailed(compiled.getMessages());
        }

        // add defined types
        var pkg = compiled.getProgram().getPackageDefinition();
        var module = context.modules.getOrCreateModule(pkg.getModuleName());

        for (TypeAlias typeAlias : compiled.getProgram().getTypeAliases()) {
            module.defineType(pkg.getPackageName(), typeAlias);
        }

        // convert code
        var fdBuilder = FrameDescriptor.newBuilder();
        var converter = new Converter(this, fdBuilder);
        var executableAst = converter.convertProgram(compiled.getProgram());
        var rootNode = new ProgramRootNode(this, context, executableAst, fdBuilder.build());
        return rootNode.getCallTarget();
    }
}
