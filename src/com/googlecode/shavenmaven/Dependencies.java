package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
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
import static com.googlecode.totallylazy.Files.recursiveFiles;
import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.predicates.Predicates.in;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.not;
import static com.googlecode.totallylazy.predicates.Predicates.where;

public class Dependencies {
    private final Sequence<Artifact> artifacts;
    private final PrintStream out;

    public Dependencies(Iterable<Artifact> artifacts, PrintStream out) {
        this.out = out;
        this.artifacts = sequence(artifacts);
    }

    @Deprecated
    public static Dependencies load(File file) throws IOException {
        return load(file, System.out);
    }

    public static Dependencies load(File file, PrintStream out) {
        return new Dependencies(SupportedArtifacts.supportedArtifacts().artifacts(some(file)), out);
    }

    public Sequence<Option<String>> update(File directory) {
        files(directory).
                filter(where(name(), is(not(in(artifacts.map(Artifact::filename))))).and(not(isDirectory()))).
                map(delete()).realise();
        final Resolver resolver = new Resolver(directory, out);
        return artifacts.filter(not(Artifact.functions.existsIn(directory))).mapConcurrently(resolve(resolver));
    }

    public static void main(String[] args) throws Exception {
        Pair<Sequence<String>, Sequence<String>> argsAndOptions = sequence(args).partition(arg -> !arg.startsWith("-"));
        Sequence<String> arguments = argsAndOptions.first();
        if (arguments.size() == 0 || arguments.size() > 2) {
            usage();
        }
        Sequence<String> options = argsAndOptions.second();
        final Configuration config = Configuration.parse(options, Dependencies::usage);
        File dependenciesFile = dependenciesFile(arguments.head());
        File directory = destinationDirectory(arguments.tail());
        Sequence<Option<String>> classpath = dependenciesFile.isDirectory() ?
                update(dependenciesFile, directory, config.out()) :
                load(dependenciesFile, config.out()).update(directory);
        boolean success = classpath.forAll(is(not(none())));
        if (success) {
            config.classpathOut().println(classpath.toString(System.getProperty("path.separator")));
        }
        System.exit(success ? 0 : 1);
    }

    @Deprecated
    public static Sequence<Option<String>> update(File dependenciesDir, final File libDir) {
        return update(dependenciesDir, libDir, System.out);
    }

    public static Sequence<Option<String>> update(File dependenciesDir, final File libDir, final PrintStream out) {
        return recursiveFiles(dependenciesDir).
                filter(hasSuffix("dependencies")).
                flatMapConcurrently(file -> load(file, out).update(directory(libDir, file.getName().replace(".dependencies", ""))));
    }

    private static File destinationDirectory(Sequence<String> arg) {
        return arg.map(asFile()).headOption().getOrElse(new File(System.getProperty("user.dir")));
    }

    private static File dependenciesFile(String arg) {
        return new File(arg);
    }

    private static void usage() {
        System.err.println("usage: [-q|--quiet] [-p|--print-classpath] dependencies[file or directory] [directory]");
        System.exit(-1);
    }

    public Sequence<Artifact> artifacts() {
        return artifacts;
    }
}
