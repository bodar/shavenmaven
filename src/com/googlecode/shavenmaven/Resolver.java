package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.ClientHttpHandler;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.handlers.RedirectHttpHandler;
import com.googlecode.utterlyidle.proxies.Proxies;

import java.io.File;
import java.io.PrintStream;

import static com.googlecode.shavenmaven.ConnectionTimeout.connectionTimeout;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Files.file;
import static com.googlecode.totallylazy.Files.write;
import static com.googlecode.utterlyidle.proxies.Proxies.autodetectProxies;
import static java.lang.String.format;

public class Resolver {
    private final File directory;
    private final PrintStream printStream;
    private final HttpClient client;

    public Resolver(File directory, PrintStream printStream, HttpClient client) {
        this.client = client;
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("'file' argument must be a directory");
        }
        this.directory = directory;
        this.printStream = printStream;
    }

    public Resolver(File directory, PrintStream printStream) {
        this(directory, printStream, new UnPackHandler(new UnGZipHandler(new RedirectHttpHandler(new ClientHttpHandler(connectionTimeout(), autodetectProxies())))));
    }

    public Resolver(File directory) {
        this(directory, System.out);
    }

    public boolean resolve(Artifact artifact) throws Exception {
        printStream.println(format("Downloading %s", artifact));
        Response response = client.handle(artifact.request());
        if (!response.status().isSuccessful()) {
            printStream.println(format("Failed to download %s (%s)", artifact, response.status()));
            return false;
        }
        using(response.entity().inputStream(), write(file(directory, artifact.filename())));
        return true;
    }

    public static Function1<Artifact, Boolean> resolve(final Resolver resolver) {
        return new Function1<Artifact, Boolean>() {
            public Boolean call(Artifact artifact) throws Exception {
                return resolver.resolve(artifact);
            }
        };
    }

}
