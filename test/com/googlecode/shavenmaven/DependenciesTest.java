package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.matchers.NumberMatcher;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.googlecode.shavenmaven.Dependencies.load;
import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Files.temporaryDirectory;
import static com.googlecode.totallylazy.Files.temporaryFile;
import static com.googlecode.totallylazy.Files.write;
import static com.googlecode.totallylazy.Sequences.sequence;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DependenciesTest {
    @Test
    public void removesAllFilesNotInUrlList() throws Exception{
        File temporaryDirectory = temporaryDirectory();
        temporaryFile(temporaryDirectory);
        temporaryFile(temporaryDirectory);
        assertThat(files(temporaryDirectory).size(), NumberMatcher.is(2));

        load(listOfDependenciesInAFile()).update(temporaryDirectory);

        Sequence<File> files = files(temporaryDirectory);
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(new File(temporaryDirectory, "yatspec-87.jar")), is(true));
    }

    @Test
    public void supportsLoadingFromFile() throws Exception{
        File temporaryDirectory = temporaryDirectory();

        load(listOfDependenciesInAFile()).update(temporaryDirectory);

        Sequence<File> files = files(temporaryDirectory);
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(new File(temporaryDirectory, "dependency.txt")), is(true));
    }

    private File listOfDependenciesInAFile() throws IOException {
        File temporaryFile = temporaryFile();
        String fileContents = getClass().getResource("dependency.txt").toString() + "\n";
        write(fileContents.getBytes(), temporaryFile);
        return temporaryFile;
    }

    @Test
    public void supportsIgnoresEmptyLines() throws Exception{
        File temporaryDirectory = temporaryDirectory();
        File list = listOfDependenciesInAFile();
        write("\n".getBytes(), list);

        load(list).update(temporaryDirectory);

        Sequence<File> files = sequence(temporaryDirectory.listFiles());
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(new File(temporaryDirectory, "dependency.txt")), is(true));
    }
}
