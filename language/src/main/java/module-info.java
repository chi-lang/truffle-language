module gh.marad.chi.language {
  requires java.base;
  requires java.logging;
  requires jdk.unsupported;
  requires org.antlr.antlr4.runtime;
  requires org.graalvm.polyglot;
  requires org.graalvm.truffle;
  requires chi.compiler;
  requires kotlin.stdlib;
  requires org.jgrapht.core;

  exports gh.marad.chi.language;

  provides com.oracle.truffle.api.provider.TruffleLanguageProvider with
    gh.marad.chi.language.ChiLanguageProvider;
}