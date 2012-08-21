package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Sequence;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static com.googlecode.shavenmaven.Artifacts.*;
import static com.googlecode.shavenmaven.Resolver.resolve;
import static com.googlecode.totallylazy.Callables.curry;
import static com.googlecode.totallylazy.Files.*;
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
        return new Dependencies(artifacts(file), out);
    }

    public boolean update(File directory) {
        files(directory).
                filter(where(name(), is(not(in(artifacts.map(asFilename()))))).and(not(isDirectory()))).
                map(delete()).realise();
        final Resolver resolver = new Resolver(directory, out);
        return artifacts.filter(not(existsIn(directory))).mapConcurrently(resolve(resolver)).forAll(is(true));
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args.length > 2) {
            System.err.println("usage: dependencies.file [directory]");
            System.exit(-1);
        }
        Sequence<String> arguments = sequence(args);
        boolean success = load(dependenciesFile(arguments.head())).update(destinationDirectory(arguments.tail()));
        System.exit(success ? 0 : 1);
    }

    private static File destinationDirectory(Sequence<String> arg) {
        return arg.map(asFile()).add(new File(System.getProperty("user.dir"))).head();
    }

    private static File dependenciesFile(String arg) {
        return new File(arg);
    }

}
