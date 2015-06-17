package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Request;

import java.io.File;

import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Files.name;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.where;

public interface Artifact {
    String group();

    String id();

    String version();

    String type();

    Uri uri();

    Request request();

    String filename();

    Uri value();

    static class methods {
        public static Function1<Artifact, String> type() {
            return new Function1<Artifact, String>() {
                public String call(Artifact artifact) throws Exception {
                    return artifact.type();
                }
            };
        }
    }

    static class functions {
        public static Mapper<Artifact, String> asFilename = new Mapper<Artifact, String>() {
            public String call(Artifact uri) throws Exception {
                return uri.filename();
            }
        };

        public static Predicate<Artifact> existsIn(final File directory) {
            return new Predicate<Artifact>() {
                public boolean matches(Artifact artifact) {
                    return files(directory).exists(where(name(), is(artifact.filename())));
                }
            };
        }
    }

}
