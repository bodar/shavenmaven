package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import static com.googlecode.shavenmaven.Artifacts.asArtifact;
import static com.googlecode.shavenmaven.Artifacts.asFilename;
import static com.googlecode.shavenmaven.Artifacts.existsIn;
import static com.googlecode.totallylazy.Callables.curry;
import static com.googlecode.totallylazy.Callers.callConcurrently;
import static com.googlecode.totallylazy.Files.asFile;
import static com.googlecode.totallylazy.Files.delete;
import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Files.name;
import static com.googlecode.totallylazy.Predicates.in;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.empty;
import static com.googlecode.totallylazy.Strings.lines;

public class Dependencies {
    private final Sequence<Artifact> artifacts;

    public Dependencies(Iterable<Artifact> artifacts) {
        this.artifacts = sequence(artifacts);
    }

    public static Dependencies load(File file) throws IOException {
        return new Dependencies(lines(file).filter(not(empty())).map(asArtifact()).memorise());
    }

    public Dependencies update(File directory) {
        files(directory).filter(where(name(), is(not(in(artifacts.map(asFilename())))))).map(delete()).realise();
        final Resolver resolver = new Resolver(directory);
        try {
            callConcurrently(artifacts.filter(not(existsIn(directory))).map(resolve(resolver)));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private Callable1<Artifact, Callable<Resolver>> resolve(final Resolver resolver) {
        return new Callable1<Artifact, Callable<Resolver>>() {
            public Callable<Resolver> call(Artifact artifact) throws Exception {
                return curry(Resolver.resolve(resolver), artifact);
            }
        };
    }

    public static void main(String[] args) throws IOException {
        if(args.length == 0 || args.length > 2){
            System.err.println("usage: dependencies.file [directory]");
            System.exit(-1);
        }
        Sequence<String> arguments = sequence(args);
        load(dependenciesFile(arguments.head())).update(destinationDirectory(arguments.tail()));
    }

    private static File destinationDirectory(Sequence<String> arg) {
        return arg.map(asFile()).add(new File(System.getProperty("user.dir"))).head();
    }

    private static File dependenciesFile(String arg) {
        return new File(arg);
    }

}
