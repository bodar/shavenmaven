package com.googlecode.shavenmaven;

import com.googlecode.shavenmaven.mvn.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Sequences.sequence;
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
        artifact.writeTo(directory);
        return this;
    }

    public static void write(byte[] bytes, File file) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(bytes);
        outputStream.close();
    }

    private File file(Artifact url) {
        return new File(directory, url.filename());
    }
}
