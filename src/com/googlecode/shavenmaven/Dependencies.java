package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Sequence;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static com.googlecode.shavenmaven.Resolver.resolve;
import static com.googlecode.totallylazy.Files.*;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.totallylazy.Sequences.sequence;

public class Dependencies {
    private final Sequence<Artifact> artifacts;
    private final PrintStream out;

    public Dependencies(Iterable<Artifact> artifacts, PrintStream out) {
        this.out = out;
        this.artifacts = sequence(artifacts);
    }

    public static Dependencies load(File file) throws IOException {
        return load(file, System.out);
    }

    public static Dependencies load(File file, PrintStream out) {
        return new Dependencies(SupportedArtifacts.supportedArtifacts().artifacts(some(file)), out);
    }

    public boolean update(File directory) {
        files(directory).
                filter(where(name(), is(not(in(artifacts.map(Artifact.functions.asFilename))))).and(not(isDirectory()))).
                map(delete()).realise();
        final Resolver resolver = new Resolver(directory, out);
        return artifacts.filter(not(Artifact.functions.existsIn(directory))).mapConcurrently(resolve(resolver)).forAll(is(true));
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || args.length > 2) {
            System.err.println("usage: dependencies.file [directory]");
            System.exit(-1);
        }
        Sequence<String> arguments = sequence(args);
        final File dependenciesFile = dependenciesFile(arguments.head());
        boolean success = load(dependenciesFile).update(destinationDirectory(arguments.tail()));
        System.exit(success ? 0 : 1);
    }

    private static File destinationDirectory(Sequence<String> arg) {
        return arg.map(asFile()).add(new File(System.getProperty("user.dir"))).head();
    }

    private static File dependenciesFile(String arg) {
        return new File(arg);
    }
}
