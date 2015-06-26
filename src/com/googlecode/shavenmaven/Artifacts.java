package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.functions.Function1;

public interface Artifacts {
    String scheme();

    Iterable<? extends Artifact> parse(String value);

    class functions {
        public static Function1<String, Iterable<? extends Artifact>> asArtifact(final Artifacts artifacts) {
            return value -> artifacts.parse(value);
        }
    }
}