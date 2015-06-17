package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;

public interface Artifacts {
    String scheme();

    Iterable<? extends Artifact> parse(String value);

    class functions {
        public static Function1<String, Iterable<? extends Artifact>> asArtifact(final Artifacts artifacts) {
            return new Function1<String, Iterable<? extends Artifact>>() {
                public Iterable<? extends Artifact> call(String value) throws Exception {
                    return artifacts.parse(value);
                }
            };
        }
    }
}