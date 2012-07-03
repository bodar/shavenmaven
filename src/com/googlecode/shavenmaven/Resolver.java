package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Files.write;
import static java.lang.String.format;

public class Resolver {
    private final File directory;
    private final PrintStream printStream;

    public Resolver(File directory, PrintStream printStream) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("'file' argument must be a directory");
        }
        this.directory = directory;
        this.printStream = printStream;
    }

    public Resolver(File directory) {
        this(directory, System.out);
    }

    public boolean resolve(Artifact artifact) throws IOException {
        printStream.println(format("Downloading %s", artifact));
        try {
            write(bytes(artifact.inputStream()), new File(directory, artifact.filename()));
            return true;
        } catch (IOException e) {
            printStream.println(format("Failed to download %s (%s)", artifact, e));
            return false;
        }
    }

    public static Function1<Artifact, Boolean> resolve(final Resolver resolver) {
        return new Function1<Artifact, Boolean>() {
            public Boolean call(Artifact artifact) throws Exception {
                return resolver.resolve(artifact);
            }
        };
    }
}
