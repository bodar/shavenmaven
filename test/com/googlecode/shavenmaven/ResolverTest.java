package com.googlecode.shavenmaven;

import com.sun.net.httpserver.HttpServer;
import org.junit.Test;

import java.io.*;
import java.net.MalformedURLException;

import static com.googlecode.shavenmaven.Artifacts.artifact;
import static com.googlecode.shavenmaven.DependenciesTest.DEPENDENCY_FILENAME;
import static com.googlecode.shavenmaven.DependenciesTest.dependencyFrom;
import static com.googlecode.shavenmaven.Http.createHttpsServer;
import static com.googlecode.shavenmaven.Http.returnResponse;
import static com.googlecode.shavenmaven.Http.urlOf;
import static com.googlecode.totallylazy.Files.temporaryDirectory;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResolverTest {
    @Test
    public void resolvesArtifact() throws Exception{
        HttpServer server = createHttpsServer(returnResponse(200, "Some dependency content"));

        File directory = temporaryDirectory();
        Resolver resolver = new Resolver(directory);

        assertThat(directory.listFiles().length, is(0));
        resolver.resolve(dependencyFrom(server));
        File[] files = directory.listFiles();
        assertThat(files.length, is(1));
        assertThat(files[0].getName(), is(DEPENDENCY_FILENAME));
    }

    @Test
    public void handlesNotFound() throws Exception{
        HttpServer server = createHttpsServer(returnResponse(404, "Not found"));

        File directory = temporaryDirectory();
        ByteArrayOutputStream log = new ByteArrayOutputStream();
        Resolver resolver = new Resolver(directory, new PrintStream(log));

        resolver.resolve(dependencyFrom(server));
        assertThat(log.toString(), containsString("Failed to download"));

    }

}
