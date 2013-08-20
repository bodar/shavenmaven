package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Rules;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.ClientHttpHandler;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.handlers.RedirectHttpHandler;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.zip.GZIPInputStream;

import static com.googlecode.shavenmaven.ConnectionRules.connectByUrlRules;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Files.file;
import static com.googlecode.totallylazy.Files.write;
import static java.lang.String.format;

public class Resolver {
    private final File directory;
    private final PrintStream printStream;
    private final Rules<Artifact, Request> connectionRules;
    private final HttpClient client;

    public Resolver(File directory, PrintStream printStream, Rules<Artifact, Request> connectionRules, HttpClient client) {
        this.client = client;
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("'file' argument must be a directory");
        }
        this.directory = directory;
        this.printStream = printStream;
        this.connectionRules = connectionRules;
    }

    public Resolver(File directory, PrintStream printStream, Rules<Artifact, Request> rules) {
        this(directory, printStream, rules, new RedirectHttpHandler(new ClientHttpHandler()));
    }

    public Resolver(File directory, PrintStream printStream) {
        this(directory, printStream, connectByUrlRules());
    }

    public Resolver(File directory) {
        this(directory, System.out);
    }

    public boolean resolve(Artifact artifact) throws Exception {
        printStream.println(format("Downloading %s", artifact));
        Request request = connectionRules.call(artifact);
        Response response = client.handle(request);
        if (!response.status().isSuccessful()) {
            printStream.println(format("Failed to download %s (%s)", artifact, response.status()));
            return false;
        }
        InputStream inputStream = response.entity().inputStream();
        using("gzip".equalsIgnoreCase(response.headers().getValue(HttpHeaders.CONTENT_ENCODING)) ? new GZIPInputStream(inputStream) : inputStream, write(file(directory, artifact.filename())));
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
