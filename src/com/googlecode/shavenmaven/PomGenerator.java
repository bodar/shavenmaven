package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.None;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Strings;

import java.io.File;
import java.io.IOException;

import static com.googlecode.shavenmaven.Artifact.methods.type;
import static com.googlecode.totallylazy.Files.write;
import static com.googlecode.totallylazy.None.none;
import static com.googlecode.totallylazy.Option.option;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.String.format;

public class PomGenerator {
    public String generate(Artifact artifact, Iterable<? extends Artifact> dependencies) {
        return applyTemplate("pom", artifact.group(), artifact.id(), artifact.version(),
                sequence(dependencies).filter(where(type(), is("jar"))).map(template("dependency")).toString(""));
    }

    private Function1<Artifact, String> template(final String name) {
        return new Function1<Artifact, String>() {
            public String call(Artifact artifact) throws Exception {
                return applyTemplate(name, artifact.group(), artifact.id(), artifact.version());
            }
        };
    }

    private String applyTemplate(String name, Object... arguments) {
        return format(Strings.toString(getClass().getResourceAsStream(name + ".template")), arguments);
    }

    public static void main(String[] args) throws IOException {
        if (!(2 <= args.length && args.length <= 3)) {
            System.err.println("usage: artifact:uri [dependencies.file] pom.directory");
            System.exit(-1);
        }
        Artifact artifact = sequence(Artifacts.artifact(args[0])).head();
        String pom = new PomGenerator().generate(artifact, Artifacts.artifacts(dependencies(args)));
        write(pom.getBytes(), new File(pomDirectory(args), pomfile(artifact)));
    }

    private static String pomDirectory(String[] args) {
        return sequence(args).last();
    }

    private static Option<File> dependencies(String[] args) {
        return args.length == 3 ? some(new File(args[1])) : none(File.class);
    }

    private static String pomfile(Artifact artifact) {
        return format("%s-%s.pom", artifact.id(), artifact.version());
    }
}