package com.googlecode.shavenmaven;

import com.googlecode.shavenmaven.config.SectionedProperties;
import com.googlecode.shavenmaven.s3.AwsCredentialsParser;
import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Rules;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.utterlyidle.Request;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static com.googlecode.shavenmaven.ConnectionRules.connectByUrlRules;
import static com.googlecode.shavenmaven.Resolver.resolve;
import static com.googlecode.shavenmaven.config.SectionedProperties.sectionedProperties;
import static com.googlecode.shavenmaven.s3.S3ConnectionRules.authenticatedS3ConnectionRule;
import static com.googlecode.totallylazy.Files.*;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.string;
import static java.lang.System.getProperty;

public class Dependencies {
    private final Sequence<Artifact> artifacts;
    private final PrintStream out;
    private final Rules<Artifact, Request> connectionRules;

    public Dependencies(Iterable<Artifact> artifacts, PrintStream out, final Rules<Artifact, Request> connectionRules) {
        this.out = out;
        this.artifacts = sequence(artifacts);
        this.connectionRules = connectionRules;
    }

    public static Dependencies load(File file) throws IOException {
        return load(file, connectByUrlRules());
    }

    public static Dependencies load(File file, Rules<Artifact, Request> connectionRules) throws IOException {
        return load(file, System.out, connectionRules);
    }

    public static Dependencies load(File file, PrintStream out, Rules<Artifact, Request> connectionRules) {
        return new Dependencies(Artifacts.constructors.artifacts(some(file)), out, connectionRules);
    }

    public boolean update(File directory) {
        files(directory).
                filter(where(name(), is(not(in(artifacts.map(Artifact.functions.asFilename))))).and(not(isDirectory()))).
                map(delete()).realise();
        final Resolver resolver = new Resolver(directory, out, connectionRules);
        return artifacts.filter(not(Artifacts.functions.existsIn(directory))).mapConcurrently(resolve(resolver)).forAll(is(true));
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || args.length > 2) {
            System.err.println("usage: dependencies.file [directory]");
            System.exit(-1);
        }
        Sequence<String> arguments = sequence(args);
        final File dependenciesFile = dependenciesFile(arguments.head());
        boolean success = load(dependenciesFile, connectionRules()).update(destinationDirectory(arguments.tail()));
        System.exit(success ? 0 : 1);
    }

    private static Rules<Artifact, Request> connectionRules() throws Exception {
        return connectByUrlRules().addFirst(authenticatedS3ConnectionRule(AwsCredentialsParser.awsCredentials(smrcProperties())));
    }

    private static File destinationDirectory(Sequence<String> arg) {
        return arg.map(asFile()).add(new File(System.getProperty("user.dir"))).head();
    }

    private static File dependenciesFile(String arg) {
        return new File(arg);
    }

    public static SectionedProperties smrcProperties() {
        return sectionedProperties(fileOption(new File(getProperty("user.home")), ".smrc").map(read()).getOrElse(""));
    }

    private static Mapper<File,String> read() {
        return new Mapper<File, String>() {
            @Override
            public String call(File file) throws Exception {
                return string(file);
            }
        };
    }


}
