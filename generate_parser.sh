#!/usr/bin/env bash
curl -O https://www.antlr.org/download/antlr-4.12.0-complete.jar
$JAVA_HOME/bin/java -cp antlr-4.12.0-complete.jar org.antlr.v4.Tool \
    -visitor -no-listener \
    -package gh.marad.chi.core.antlr \
    -o language/src/main/
    language/src/main/antlr/ChiParser.g4