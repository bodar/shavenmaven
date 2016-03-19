package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.functions.Block;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Option;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.handlers.ClientHttpHandler;
import com.googlecode.utterlyidle.handlers.HttpClient;
import com.googlecode.utterlyidle.handlers.RedirectHttpHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

import static com.googlecode.shavenmaven.ConnectionTimeout.connectionTimeout;
import static com.googlecode.shavenmaven.UnGZipHandler.gzipInputStream;
import static com.googlecode.totallylazy.functions.Block.block;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Files.file;
import static com.googlecode.totallylazy.Files.write;
import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.option;
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
        this(directory, printStream, new UserAgentHandler(new UnGZipHandler(new RedirectHttpHandler(new ClientHttpHandler(connectionTimeout(), autodetectProxies())))));
    }

    @Deprecated
    public Resolver(File directory) {
        this(directory, System.out);
    }

    public Option<String> resolve(Artifact artifact) throws Exception {
        printStream.println(format("Downloading %s", artifact));
        Response response = client.handle(artifact.request());
        if (!response.status().isSuccessful()) {
            printStream.println(format("Failed to download %s (%s)", artifact, response.status()));
            return none();
        }
        return handle(artifact, response);
    }

    private Option<String> handle(final Artifact artifact, final Response response) throws IOException {
        File part = file(directory, artifact.filename() + ".part");
        File destination = new File(directory, artifact.filename());
        using(response.entity().inputStream(),
                artifact.uri().path().endsWith(".pack.gz") ?
                        unpack(part) :
                        write(part));
        if(!part.renameTo(destination)) {
            printStream.println(format("Failed to rename %s to %s", part, destination));
            return none();
        }
        return option(destination.getPath());
    }

    private Block<InputStream> unpack(final File file) {
        return input -> {
            printStream.println(format("Unpacking %s", file));
            using(new JarOutputStream(new FileOutputStream(file)),
                    block(output -> Pack200.newUnpacker().unpack(gzipInputStream(input), output)));
        };
    }

    public static Function1<Artifact, Option<String>> resolve(final Resolver resolver) {
        return resolver::resolve;
    }

}
