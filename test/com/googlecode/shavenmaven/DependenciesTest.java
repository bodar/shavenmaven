package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Files;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.matchers.NumberMatcher;
import com.sun.net.httpserver.HttpServer;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static com.googlecode.shavenmaven.Dependencies.load;
import static com.googlecode.shavenmaven.Http.*;
import static com.googlecode.totallylazy.Files.*;
import static com.googlecode.totallylazy.Sequences.sequence;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DependenciesTest {
    static final String DEPENDENCY_FILENAME = "dependency.txt";
    private HttpServer server;

    public static Artifact dependencyFrom(HttpServer server) throws MalformedURLException {
        return UrlArtifacts.instance.parse(urlOf(server) + DEPENDENCY_FILENAME).head();
    }

    @Before
    public void setUp() throws Exception {
        server = createHttpsServer(returnResponse(200, "Some dependency content"));
    }

    @Test
    public void doesNotRemoveDirectoriesInUpdateDirectory() throws Exception{
        File temporaryDirectory = temporaryDirectory();
        File subDirectory = new File(temporaryDirectory, randomFilename());
        subDirectory.mkdir();
        temporaryFile(temporaryDirectory);
        assertThat(files(temporaryDirectory).size(), NumberMatcher.is(2));

        assertThat(load(listOfDependenciesInAFile()).update(temporaryDirectory), is(true));

        Sequence<File> files = files(temporaryDirectory);
        assertThat(files.size(), NumberMatcher.is(2));
        assertThat(files.contains(new File(temporaryDirectory, DEPENDENCY_FILENAME)), is(true));
        assertThat(files.contains(subDirectory), is(true));
    }

    private File temporaryDirectory() {
        return Files.emptyTemporaryDirectory(DependenciesTest.class.getSimpleName());
    }

    @Test
    public void removesAllFilesNotInUrlList() throws Exception{
        File temporaryDirectory = temporaryDirectory();
        temporaryFile(temporaryDirectory);
        temporaryFile(temporaryDirectory);
        assertThat(files(temporaryDirectory).size(), NumberMatcher.is(2));

        assertThat(load(listOfDependenciesInAFile()).update(temporaryDirectory), is(true));

        Sequence<File> files = files(temporaryDirectory);
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(new File(temporaryDirectory, DEPENDENCY_FILENAME)), is(true));
    }

    @Test
    public void supportsLoadingFromFile() throws Exception{
        File temporaryDirectory = temporaryDirectory();

        assertThat(load(listOfDependenciesInAFile()).update(temporaryDirectory), is(true));

        Sequence<File> files = files(temporaryDirectory);
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(new File(temporaryDirectory, DEPENDENCY_FILENAME)), is(true));
    }

    @Test
    public void reportsFailures() throws Exception{
        File temporaryDirectory = temporaryDirectory();
        server = createHttpsServer(returnResponse(404, "Not Found"));

        assertThat(load(listOfDependenciesInAFile()).update(temporaryDirectory), is(false));
    }

    @Test
    public void ignoresEmptyLines() throws Exception{
        File temporaryDirectory = temporaryDirectory();
        File list = temporaryFile();
        write((dependencyFrom(server) + "\n\n\n").getBytes(), list);

        assertThat(load(list).update(temporaryDirectory), is(true));

        Sequence<File> files = sequence(temporaryDirectory.listFiles());
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(new File(temporaryDirectory, DEPENDENCY_FILENAME)), is(true));
    }

    private File listOfDependenciesInAFile() throws IOException {
        File temporaryFile = temporaryFile();
        String fileContents = dependencyFrom(server) + "\n";
        write(fileContents.getBytes(), temporaryFile);
        return temporaryFile;
    }


}
