package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Files;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.matchers.NumberMatcher;
import com.sun.net.httpserver.HttpServer;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static java.nio.file.Files.createTempDirectory;

import static com.googlecode.shavenmaven.Dependencies.load;
import static com.googlecode.shavenmaven.Dependencies.update;
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

        File dependencyFile = new File(temporaryDirectory, DEPENDENCY_FILENAME);
        assertThat(load(listOfDependenciesInAFile(), System.out).update(temporaryDirectory).map(Option::get), is(sequence(dependencyFile.getPath())));

        Sequence<File> files = files(temporaryDirectory);
        assertThat(files.size(), NumberMatcher.is(2));
        assertThat(files.contains(dependencyFile), is(true));
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

        File dependencyFile = new File(temporaryDirectory, DEPENDENCY_FILENAME);
        assertThat(load(listOfDependenciesInAFile(), System.out).update(temporaryDirectory).map(Option::get), is(sequence(dependencyFile.getPath())));

        Sequence<File> files = files(temporaryDirectory);
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(dependencyFile), is(true));
    }

    @Test
    public void supportsLoadingFromFile() throws Exception{
        File temporaryDirectory = temporaryDirectory();

        File dependencyFile = new File(temporaryDirectory, DEPENDENCY_FILENAME);
        assertThat(load(listOfDependenciesInAFile(), System.out).update(temporaryDirectory).map(Option::get), is(sequence(dependencyFile.getPath())));

        Sequence<File> files = files(temporaryDirectory);
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(dependencyFile), is(true));
    }

    @Test
    public void loadsFromFilesInDirectory() throws Exception{
        File dependenciesDirectory = temporaryDirectory();
        File dependenciesFile = listOfDependenciesInAFile(File.createTempFile("test", ".dependencies", dependenciesDirectory));

        String filenameWithoutSuffix = dependenciesFile.getName();
        int extensionStartsAt = filenameWithoutSuffix.lastIndexOf(".");
        if (extensionStartsAt > 0) {
            filenameWithoutSuffix = filenameWithoutSuffix.substring(0, extensionStartsAt);
        }

        File libOutputDirectory = createTempDirectory("testLibOutput").toFile();
        File targetOutputDirectory = new File(libOutputDirectory, filenameWithoutSuffix);

        final File dependencyFile = new File(targetOutputDirectory, DEPENDENCY_FILENAME);
        assertThat(update(dependenciesDirectory, libOutputDirectory, System.out).map(Option::get), is(sequence(dependencyFile.getPath())));

        Sequence<File> outputDirectories = files(libOutputDirectory);
        assertThat(outputDirectories.size(), NumberMatcher.is(1));
        assertThat(outputDirectories.contains(targetOutputDirectory), is(true));

        Sequence<File> files = files(targetOutputDirectory);
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(dependencyFile), is(true));
    }

    @Test
    public void recursivelyLoadsDependenciesFilesUnderDirectory() throws Exception {
        File dependenciesDirectory = temporaryDirectory();

        File firstNestedDependenciesDirectory = createTempDirectory(dependenciesDirectory.toPath(), "first").toFile();
        File firstDependenciesFile = listOfDependenciesInAFile(File.createTempFile("test-first-", ".dependencies", firstNestedDependenciesDirectory));
        String firstDependenciesFilenameWithoutSuffix = firstDependenciesFile.getName();
        int firstDependenciesFilenameExtensionStartsAt = firstDependenciesFilenameWithoutSuffix.lastIndexOf(".");
        if (firstDependenciesFilenameExtensionStartsAt > 0) {
            firstDependenciesFilenameWithoutSuffix = firstDependenciesFilenameWithoutSuffix.substring(0, firstDependenciesFilenameExtensionStartsAt);
        }

        File secondNestedDependenciesDirectory = createTempDirectory(dependenciesDirectory.toPath(), "second").toFile();
        File secondDependenciesFile = listOfDependenciesInAFile(File.createTempFile("test-second-", ".dependencies", secondNestedDependenciesDirectory));
        String secondDependenciesFilenameWithoutSuffix = secondDependenciesFile.getName();
        int secondDependenciesFilenameExtensionStartsAt = secondDependenciesFilenameWithoutSuffix.lastIndexOf(".");
        if (secondDependenciesFilenameExtensionStartsAt > 0) {
            secondDependenciesFilenameWithoutSuffix = secondDependenciesFilenameWithoutSuffix.substring(0, secondDependenciesFilenameExtensionStartsAt);
        }

        File libOutputDirectory = createTempDirectory("testLibOutput").toFile();
        File firstTargetOutputDirectory = new File(libOutputDirectory, firstDependenciesFilenameWithoutSuffix);
        File secondTargetOutputDirectory = new File(libOutputDirectory, secondDependenciesFilenameWithoutSuffix);

        File firstDependencyFile = new File(firstTargetOutputDirectory, DEPENDENCY_FILENAME);
        File secondDependencyFile = new File(secondTargetOutputDirectory, DEPENDENCY_FILENAME);

        assertThat(update(dependenciesDirectory, libOutputDirectory, System.out).map(Option::get).toSortedList(String.CASE_INSENSITIVE_ORDER), is(sequence(firstDependencyFile.getPath(), secondDependencyFile.getPath()).toSortedList(String.CASE_INSENSITIVE_ORDER)));

        Sequence<File> outputDirectories = files(libOutputDirectory);
        assertThat(outputDirectories.size(), NumberMatcher.is(2));

        Sequence<File> firstTargetOutputFiles = files(firstTargetOutputDirectory);
        assertThat(firstTargetOutputFiles.size(), NumberMatcher.is(1));
        assertThat(firstTargetOutputFiles.contains(firstDependencyFile), is(true));

        Sequence<File> secondTargetOutputFiles = files(secondTargetOutputDirectory);
        assertThat(secondTargetOutputFiles.size(), NumberMatcher.is(1));
        assertThat(secondTargetOutputFiles.contains(secondDependencyFile), is(true));
    }

    @Test
    public void reportsFailures() throws Exception{
        File temporaryDirectory = temporaryDirectory();
        server = createHttpsServer(returnResponse(404, "Not Found"));

        assertThat(load(listOfDependenciesInAFile(), System.out).update(temporaryDirectory).map(Option::isEmpty), is(sequence(true)));
    }

    @Test
    public void ignoresEmptyLines() throws Exception{
        File temporaryDirectory = temporaryDirectory();
        File list = temporaryFile();
        write((dependencyFrom(server) + "\n\n\n").getBytes(), list);

        File dependencyFile = new File(temporaryDirectory, DEPENDENCY_FILENAME);
        assertThat(load(list, System.out).update(temporaryDirectory).map(Option::get), is(sequence(dependencyFile.getPath())));

        Sequence<File> files = sequence(temporaryDirectory.listFiles());
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(dependencyFile), is(true));
    }

    private File listOfDependenciesInAFile() throws IOException {
        File temporaryFile = temporaryFile();
        return listOfDependenciesInAFile(temporaryFile);
    }

    private File listOfDependenciesInAFile(final File temporaryFile) throws MalformedURLException {
        String fileContents = dependencyFrom(server) + "\n";
        write(fileContents.getBytes(), temporaryFile);
        return temporaryFile;
    }

}
