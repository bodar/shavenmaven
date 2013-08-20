package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;

import java.io.File;

import static com.googlecode.shavenmaven.CompositeArtifacts.compositeArtifacts;
import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Files.name;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.flatten;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.empty;
import static com.googlecode.totallylazy.Strings.lines;
import static com.googlecode.totallylazy.Strings.startsWith;

public interface Artifacts {
    String scheme();
    Iterable<? extends Artifact> parse(String value);

    class constructors {
        static Artifacts supported = compositeArtifacts(sequence(MvnArtifacts.instance, S3Artifacts.instance, UrlArtifacts.instance));

        public static Artifact artifact(String value) {
            return sequence(supported.parse(value)).head();
        }

        public static Sequence<Artifact> artifacts(Option<File> file) {
            return flatten(file.map(toArtifacts()));
        }

        private static Callable1<File, Sequence<Artifact>> toArtifacts() {
            return new Callable1<File, Sequence<Artifact>>() {
                public Sequence<Artifact> call(File file) throws Exception {
                    return toArtifacts(lines(file));
                }
            };
        }

        public static Sequence<Artifact> toArtifacts(Sequence<String> lines) {
            return lines.filter(not(empty().or(startsWith("#")))).flatMap(functions.asArtifact()).memorise();
        }

    }

    class functions {
        public static Predicate<Artifact> existsIn(final File directory) {
            return new Predicate<Artifact>() {
                public boolean matches(Artifact artifact) {
                    return files(directory).exists(where(name(), is(artifact.filename())));
                }
            };
        }
        public static Function1<String, Iterable<? extends Artifact>> asArtifact() {
            return new Function1<String, Iterable<? extends Artifact>>() {
                public Iterable<? extends Artifact> call(String value) throws Exception {
                    return constructors.supported.parse(value);
                }
            };
        }
    }

}
