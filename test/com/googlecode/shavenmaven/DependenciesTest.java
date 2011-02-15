package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.matchers.NumberMatcher;
import org.junit.Test;

import java.io.File;

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
        File temporaryFile = temporaryFile();
        write("http://yatspec.googlecode.com/files/yatspec-87.jar\n".getBytes(), temporaryFile);
        Dependencies dependencies = load(temporaryFile);
        File temporaryDirectory = temporaryDirectory();
        temporaryFile(temporaryDirectory);
        dependencies.update(temporaryDirectory);
        Sequence<File> files = files(temporaryDirectory);
        assertThat(files.size(), NumberMatcher.is(1));
        assertThat(files.contains(new File(temporaryDirectory, "yatspec-87.jar")), is(true));
    }

    @Test
    public void supportsLoadingFromFile() throws Exception{
        File temporaryFile = temporaryFile();
        String fileContents = "http://yatspec.googlecode.com/files/yatspec-87.jar\n" +
                              "mvn://repo.bodar.com/com.googlecode.yadic:yadic:jar:116\n" +
                              "mvn:org.objenesis:objenesis:jar:1.2";
        write(fileContents.getBytes(), temporaryFile);
        Dependencies dependencies = load(temporaryFile);
        File temporaryDirectory = temporaryDirectory();
        dependencies.update(temporaryDirectory);
        Sequence<File> files = sequence(temporaryDirectory.listFiles());
        assertThat(files.size(), NumberMatcher.is(3));
        assertThat(files.contains(new File(temporaryDirectory, "yadic-116.jar")), is(true));
        assertThat(files.contains(new File(temporaryDirectory, "yatspec-87.jar")), is(true));
    }

    @Test
    public void supportsIgnoresEmptyLines() throws Exception{
        File temporaryFile = temporaryFile();
        String fileContents = "http://yatspec.googlecode.com/files/yatspec-87.jar\n" +
                              "\n" +
                              "mvn:org.objenesis:objenesis:jar:1.2\n";
        write(fileContents.getBytes(), temporaryFile);
        Dependencies dependencies = load(temporaryFile);
        File temporaryDirectory = temporaryDirectory();
        dependencies.update(temporaryDirectory);
        Sequence<File> files = sequence(temporaryDirectory.listFiles());
        assertThat(files.size(), NumberMatcher.is(2));
        assertThat(files.contains(new File(temporaryDirectory, "yatspec-87.jar")), is(true));
        assertThat(files.contains(new File(temporaryDirectory, "objenesis-1.2.jar")), is(true));
    }
}
