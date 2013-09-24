package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Files;
import com.googlecode.totallylazy.Strings;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.zip.GZIPOutputStream;

import static com.googlecode.shavenmaven.DependenciesTest.DEPENDENCY_FILENAME;
import static com.googlecode.shavenmaven.DependenciesTest.dependencyFrom;
import static com.googlecode.shavenmaven.Http.createHttpsServer;
import static com.googlecode.shavenmaven.Http.returnResponse;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResolverTest {
    private File temporaryDirectory() {
        return Files.emptyTemporaryDirectory(DependenciesTest.class.getSimpleName());
    }

    @Test
    public void resolvesArtifact() throws Exception {
        HttpServer server = createHttpsServer(returnResponse(200, "Some dependency content"));

        File directory = temporaryDirectory();
        Resolver resolver = new Resolver(directory);

        assertThat(directory.listFiles().length, is(0));
        assertThat(resolver.resolve(dependencyFrom(server)), is(true));
        File[] files = directory.listFiles();
        assertThat(files.length, is(1));
        assertThat(files[0].getName(), is(DEPENDENCY_FILENAME));
    }

    @Test
    public void handlesNotFound() throws Exception {
        HttpServer server = createHttpsServer(returnResponse(404, "Not found"));

        File directory = temporaryDirectory();
        ByteArrayOutputStream log = new ByteArrayOutputStream();
        Resolver resolver = new Resolver(directory, new PrintStream(log));

        assertThat(resolver.resolve(dependencyFrom(server)), is(false));
        assertThat(log.toString(), containsString("Failed to download"));
    }

    @Test
    public void handlesGzipContent() throws Exception {
        final String expectedContent = "Some dependency content";

        HttpServer server = createHttpsServer(new HttpHandler() {
            public void handle(HttpExchange httpExchange) throws IOException {
                httpExchange.getResponseHeaders().set("Content-Encoding", "gZiP");
                byte[] content = gzip(expectedContent);
                httpExchange.sendResponseHeaders(200, content.length);
                httpExchange.getResponseBody().write(content);
            }
        });

        File directory = temporaryDirectory();
        Resolver resolver = new Resolver(directory);

        boolean resolved = resolver.resolve(dependencyFrom(server));
        assertThat(resolved, is(true));

        File[] files = directory.listFiles();
        assertThat(files.length, is(1));
        assertThat(Strings.toString(new FileInputStream(files[0])), is(expectedContent));
    }

    private byte[] gzip(String message) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        GZIPOutputStream out = new GZIPOutputStream(bytes);
        out.write(message.getBytes());
        out.close();
        return bytes.toByteArray();
    }
}