package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Strings;

import java.io.File;
import java.io.IOException;

import static com.googlecode.totallylazy.Files.write;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.String.format;

public class PomGenerator {
    public String generate(Artifact artifact, Iterable<? extends Artifact> dependencies) {
        return applyTemplate("pom", artifact.group(), artifact.id(), artifact.version(),
                sequence(dependencies).map(template("dependency")).toString(""));
    }

    private Callable1<? super Artifact, String> template(final String name) {
        return new Callable1<Artifact, String>() {
            public String call(Artifact artifact) throws Exception {
                return applyTemplate(name, artifact.group(), artifact.id(), artifact.version());
            }
        };
    }

    private String applyTemplate(String name, Object... arguments) {
        return format(Strings.toString(getClass().getResourceAsStream(name + ".template")), arguments);
    }

    public static void main(String[] args) throws IOException {
        if(!(args.length == 3)){
            System.err.println("usage: artifact:uri dependencies.file pom.directory");
            System.exit(-1);
        }
        generate(args[0], new File(args[1]), new File(args[2]));
    }

    public static void generate(String uri, File dependencies, File outputDirectory) {
        Artifact artifact = sequence(Artifacts.artifact(uri)).head();
        String pom = new PomGenerator().generate(artifact, Artifacts.artifacts(dependencies));
        write(pom.getBytes(), new File(outputDirectory, pomfile(artifact)));
    }

    private static String pomfile(Artifact artifact) {
        return format("%s-%s.pom", artifact.id(), artifact.version());
    }


}
