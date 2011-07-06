package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.MemorisedSequence;
import com.googlecode.totallylazy.Predicate;

import java.io.File;

import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Files.name;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.empty;
import static com.googlecode.totallylazy.Strings.lines;

public class Artifacts {
    public static MemorisedSequence<Artifact> artifacts(File file) {
        return lines(file).filter(not(empty())).flatMap(asArtifact()).memorise();
    }

    public static Iterable<? extends Artifact> artifact(String value) {
        if(value.startsWith(MvnArtifact.PROTOCOL)){
            return MvnArtifact.parse(value);
        }
        return sequence(UrlArtifact.parse(value));
    }

    public static Callable1<? super String, Iterable<Artifact>> asArtifact() {
        return new Callable1<String, Iterable<Artifact>>() {
            public Iterable<Artifact> call(String value) throws Exception {
                return (Iterable<Artifact>) Artifacts.artifact(value);
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
