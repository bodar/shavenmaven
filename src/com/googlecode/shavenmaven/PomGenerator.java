package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Strings;

import java.io.File;
import java.io.IOException;

import static com.googlecode.shavenmaven.Artifact.methods.type;
import static com.googlecode.totallylazy.Files.write;
import static com.googlecode.totallylazy.None.none;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.bytes;
import static com.googlecode.totallylazy.Strings.endsWith;
import static java.lang.String.format;

public class PomGenerator {
    public String generate(Artifact artifact, Iterable<? extends Artifact> dependencies) {
        return applyTemplate("pom", artifact.group(), artifact.id(), artifact.version(),
                sequence(dependencies).
                        filter(not(instanceOf(UrlArtifact.class))).
                        filter(where(type(), or(is("jar"), endsWith("pack")))).
                        map(template("dependency")).toString(""));
    }

    private Function1<Artifact, String> template(final String name) {
        return new Function1<Artifact, String>() {
            public String call(Artifact artifact) throws Exception {
                return applyTemplate(name, artifact.group(), artifact.id(), artifact.version());
            }
        };
    }

    private String applyTemplate(String name, Object... arguments) {
        String template = Strings.toString(getClass().getResourceAsStream(name + ".template"));
        if(Strings.isEmpty(template)) throw new UnsupportedOperationException(format("Unable to load template %s", name));
        return format(template, arguments);
    }

    public static void main(String[] args) throws IOException {
        if (!(2 <= args.length && args.length <= 3)) {
            System.err.println("usage: artifact:uri [dependencies.file] pom.directory");
            System.exit(-1);
        }
        generate(args[0], dependencies(args), pomDirectory(args));
    }

    public static void generate(String uri, Option<File> dependencies, File outputDirectory) {
        Artifact artifact = SupportedArtifacts.supportedArtifacts().artifact(uri);
        File outputFile = new File(outputDirectory, pomfile(artifact));
        generate(artifact, dependencies, outputFile);
	}

    public static void generate(final Artifact artifact, final Option<File> dependencies, final File outputFile) {
        String pom = new PomGenerator().generate(artifact, SupportedArtifacts.supportedArtifacts().artifacts(dependencies));
        write(bytes(pom), outputFile);
    }

    private static File pomDirectory(String[] args) {
        return new File(sequence(args).last());
    }

    private static Option<File> dependencies(String[] args) {
        return args.length == 3 ? some(new File(args[1])) : none(File.class);
    }

    private static String pomfile(Artifact artifact) {
        return format("%s-%s.pom", artifact.id(), artifact.version());
    }


}
