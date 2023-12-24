module gh.marad.chi.language {
  requires java.base;
  requires java.logging;
  requires jdk.unsupported;
  requires org.antlr.antlr4.runtime;
  requires org.graalvm.polyglot;
  requires org.graalvm.truffle;
  requires compiler;
  requires kotlin.stdlib;
  requires org.jgrapht.core;

  exports gh.marad.chi.language;
  exports gh.marad.chi.language.image;
  exports gh.marad.chi.language.builtin;
  exports gh.marad.chi.language.runtime;
  exports gh.marad.chi.language.runtime.namespaces;
  exports gh.marad.chi.language.nodes;
  exports gh.marad.chi.language.nodes.value;
  exports gh.marad.chi.language.nodes.objects;
  exports gh.marad.chi.language.nodes.function;
  exports gh.marad.chi.language.nodes.expr;
  exports gh.marad.chi.language.nodes.expr.cast;
  exports gh.marad.chi.language.nodes.expr.flow;
  exports gh.marad.chi.language.nodes.expr.flow.effect;
  exports gh.marad.chi.language.nodes.expr.flow.loop;
  exports gh.marad.chi.language.nodes.expr.operators;
  exports gh.marad.chi.language.nodes.expr.operators.bit;
  exports gh.marad.chi.language.nodes.expr.operators.arithmetic;
  exports gh.marad.chi.language.nodes.expr.operators.bool;
  exports gh.marad.chi.language.nodes.expr.variables;

  provides com.oracle.truffle.api.provider.TruffleLanguageProvider with
    gh.marad.chi.language.ChiLanguageProvider;
}