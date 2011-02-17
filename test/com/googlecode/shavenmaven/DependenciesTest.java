package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.matchers.NumberMatcher;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static com.googlecode.shavenmaven.Dependencies.load;
import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Files.temporaryDirectory;
import static com.googlecode.totallylazy.Files.temporaryFile;
import static com.googlecode.totallylazy.Files.write;
import static com.googlecode.totallylazy.Sequences.sequence;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DependenciesTest {
    public static final String DEPENDENCY_FILENAME = "dependency.txt";
    public static final String DEPENDENCY_PATH = "test/com/googlecode/shavenmaven/" + DEPENDENCY_FILENAME;

    @Test
    public void removesAllFilesNotInUrlList() throws Exception{
        File temporaryDirectory = temporaryDirectory();
        temporaryFile(temporaryDirectory);
        temporaryFile(temporaryDirectory);
        assertThat(files(temporaryDirectory).size(), NumberMatcher.is(2));

        load(listOfDependenciesInAFile()).update(temporaryDirectory);

        Sequence<File> files = files(temporaryDirectory);
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(new File(temporaryDirectory, DEPENDENCY_FILENAME)), is(true));
    }

    @Test
    public void supportsLoadingFromFile() throws Exception{
        File temporaryDirectory = temporaryDirectory();

        load(listOfDependenciesInAFile()).update(temporaryDirectory);

        Sequence<File> files = files(temporaryDirectory);
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(new File(temporaryDirectory, DEPENDENCY_FILENAME)), is(true));
    }

    @Test
    public void supportsIgnoresEmptyLines() throws Exception{
        File temporaryDirectory = temporaryDirectory();
        File list = temporaryFile();
        write((dependencyUrl() + "\n\n\n").getBytes(), list);

        load(list).update(temporaryDirectory);

        Sequence<File> files = sequence(temporaryDirectory.listFiles());
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(new File(temporaryDirectory, DEPENDENCY_FILENAME)), is(true));
    }

    public static File listOfDependenciesInAFile() throws IOException {
        File temporaryFile = temporaryFile();
        String fileContents = dependencyUrl() + "\n";
        write(fileContents.getBytes(), temporaryFile);
        return temporaryFile;
    }

    public static String dependencyUrl() {
        try {
            return new File(DEPENDENCY_FILENAME).toURL().toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
