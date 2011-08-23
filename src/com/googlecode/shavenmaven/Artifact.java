package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Callable1;

import java.net.URL;

public interface Artifact {
    String group();

    String id();

    String version();

    String type();

    URL url();

    String filename();
    
    static class methods{
        public static Callable1<? super Artifact, String> type() {
            return new Callable1<Artifact, String>() {
                public String call(Artifact artifact) throws Exception {
                    return artifact.type();
                }
            };
        }
    }

}
