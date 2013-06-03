package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;

import java.net.URL;

public interface Artifact {
    String group();

    String id();

    String version();

    String type();

    URL url();

    String filename();

    String value();

    static class methods {
        public static Function1<Artifact, String> type() {
            return new Function1<Artifact, String>() {
                public String call(Artifact artifact) throws Exception {
                    return artifact.type();
                }
            };
        }
    }

}
