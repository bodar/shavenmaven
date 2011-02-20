package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.MemorisedSequence;
import com.googlecode.totallylazy.Predicate;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Files.name;
import static com.googlecode.totallylazy.Files.write;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Strings.empty;
import static com.googlecode.totallylazy.Strings.lines;

public class Artifacts {
    public static MemorisedSequence<Artifact> artifacts(File file) {
        return lines(file).filter(not(empty())).map(asArtifact()).memorise();
    }

    public static Artifact artifact(String value) {
        if(value.startsWith(MvnArtifact.PROTOCOL)){
            return new MvnArtifact(value);
        }
        return new UrlArtifact(value);

    }

    public static Callable1<? super String, Artifact> asArtifact() {
        return new Callable1<String, Artifact>() {
            public Artifact call(String value) throws Exception {
                return Artifacts.artifact(value);
            }
        };
    }

    public static Callable1<? super Artifact, String> asFilename() {
        return new Callable1<Artifact, String>() {
            public String call(Artifact uri) throws Exception {
                return uri.filename();
            }
        };
    }

    public static Predicate<? super Artifact> existsIn(final File directory) {
        return new Predicate<Artifact>() {
            public boolean matches(Artifact artifact) {
                return files(directory).exists(where(name(), is(artifact.filename())));
            }
        };
    }
}
