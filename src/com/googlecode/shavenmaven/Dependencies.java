package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Functions;
import com.googlecode.totallylazy.Sequence;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static com.googlecode.shavenmaven.Resolver.resolve;
import static com.googlecode.totallylazy.Files.asFile;
import static com.googlecode.totallylazy.Files.delete;
import static com.googlecode.totallylazy.Files.directory;
import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Files.hasSuffix;
import static com.googlecode.totallylazy.Files.isDirectory;
import static com.googlecode.totallylazy.Files.name;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Predicates.in;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
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
            System.err.println("usage: dependencies[file or directory] [directory]");
            System.exit(-1);
        }
        Sequence<String> arguments = sequence(args);
        File dependenciesFile = dependenciesFile(arguments.head());
        File directory = destinationDirectory(arguments.tail());
        boolean success = dependenciesFile.isDirectory() ?
                update(dependenciesFile, directory) :
                load(dependenciesFile).update(directory);
        System.exit(success ? 0 : 1);
    }

    public static boolean update(File dependenciesDir, final File libDir) {
        return update(dependenciesDir, libDir, System.out);
    }

    public static boolean update(File dependenciesDir, final File libDir, final PrintStream out) {
        return files(dependenciesDir).
                filter(hasSuffix("dependencies")).
                mapConcurrently(new Function1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) throws Exception {
                        return load(file, out).update(directory(libDir, file.getName().replace(".dependencies", "")));
                    }
                }).reduce(Functions.and);
    }

    private static File destinationDirectory(Sequence<String> arg) {
        return arg.map(asFile()).headOption().getOrElse(new File(System.getProperty("user.dir")));
    }

    private static File dependenciesFile(String arg) {
        return new File(arg);
    }

    public Sequence<Artifact> artifacts() {
        return artifacts;
    }
}
