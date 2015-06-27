package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.io.Uri;
import com.googlecode.utterlyidle.Request;

import java.io.File;

import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Files.name;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.where;

public interface Artifact {
    String group();

    String id();

    String version();

    String type();

    Uri uri();

    Request request();

    String filename();

    Uri value();

    class methods {
        public static Function1<Artifact, String> type() {
            return Artifact::type;
        }
    }

    class functions {
        public static Function1<Artifact, String> asFilename = Artifact::filename;

        public static Predicate<Artifact> existsIn(final File directory) {
            return artifact -> files(directory).exists(where(name(), is(artifact.filename())));
        }
    }

}
