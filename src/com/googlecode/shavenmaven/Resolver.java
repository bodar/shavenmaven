package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Callable1;

import java.io.File;
import java.io.IOException;

import static com.googlecode.shavenmaven.Artifacts.writeTo;
import static java.lang.String.format;

public class Resolver {
    private final File directory;

    public Resolver(File directory) {
        if(!directory.isDirectory()) {
            throw new IllegalArgumentException("'file' argument must be a directory");
        }
        this.directory = directory;
    }

    public Resolver resolve(Artifact artifact) throws IOException {
        System.out.println(format("Downloading %s", artifact));
        writeTo(artifact, directory);
        return this;
    }

    public static Callable1<Artifact, Resolver> resolve(final Resolver resolver) {
        return new Callable1<Artifact, Resolver>() {
            public Resolver call(Artifact artifact) throws Exception {
                return resolver.resolve(artifact);
            }
        };
    }
}
