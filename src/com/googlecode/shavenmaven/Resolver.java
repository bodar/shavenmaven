package com.googlecode.shavenmaven;

import com.googlecode.shavenmaven.mvn.Artifact;
import com.googlecode.shavenmaven.mvn.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Sequences.sequence;

public class Resolver {
    private final File directory;

    public Resolver(File directory) {
        if(!directory.isDirectory()) {
            throw new IllegalArgumentException("'file' argument must be a directory");
        }
        this.directory = directory;
    }

    public Resolver resolve(URL url) throws IOException {
        write(bytes(url.openStream()), file(url));
        return this;
    }

    public static void write(byte[] bytes, File file) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(bytes);
        outputStream.close();
    }

    private File file(URL url) {
        return new File(directory, getFilename(url));
    }

    private String getFilename(URL url) {
        if(url.getProtocol().equals(Handler.PROTOCOL)){
            return Handler.filename(Artifact.parse(url));
        }
        return sequence(url.getPath().split("/")).reverse().head();
    }

    public static URL url(String url) throws MalformedURLException {
        if(url.startsWith(Handler.PROTOCOL)){
            return new URL(null, url, new Handler());
        }
        return new URL(url);
    }
}
