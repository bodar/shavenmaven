package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;

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
    public static Sequence<Artifact> artifacts(File file) {
        return lines(file).filter(not(empty())).flatMap(asArtifact()).memorise();
    }

    public static Iterable<Artifact> artifact(String value) {
        if(value.startsWith(MvnArtifact.PROTOCOL)){
            return sequence(MvnArtifact.parse(value)).safeCast(Artifact.class);
        }
        return sequence(UrlArtifact.parse(value)).safeCast(Artifact.class);
    }

    public static Function1<String, Iterable<Artifact>> asArtifact() {
        return new Function1<String, Iterable<Artifact>>() {
            public Iterable<Artifact> call(String value) throws Exception {
                return Artifacts.artifact(value);
            }
        };
    }

    public static Function1<Artifact, String> asFilename() {
        return new Function1<Artifact, String>() {
            public String call(Artifact uri) throws Exception {
                return uri.filename();
            }
        };
    }

    public static Predicate<Artifact> existsIn(final File directory) {
        return new Predicate<Artifact>() {
            public boolean matches(Artifact artifact) {
                return files(directory).exists(where(name(), is(artifact.filename())));
            }
        };
    }
}
