package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Files;
import org.junit.Test;

import java.io.File;
import java.net.URI;

import static com.googlecode.shavenmaven.Resolver.url;
import static com.googlecode.totallylazy.Files.temporaryDirectory;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResolverTest {
    @Test
    public void supportsHttp() throws Exception{
        File directory = temporaryDirectory();
        Resolver resolver = new Resolver(directory);

        assertThat(directory.listFiles().length, is(0));
        resolver.resolve(new URI("http://yatspec.googlecode.com/files/yatspec-87.jar"));
        File[] files = directory.listFiles();
        assertThat(files.length, is(1));
        assertThat(files[0].getName(), is("yatspec-87.jar"));
    }

    @Test
    public void supportsSpecificMavenRepository() throws Exception{
        File directory = temporaryDirectory();
        Resolver resolver = new Resolver(directory);

        assertThat(directory.listFiles().length, is(0));
        resolver.resolve(new URI("mvn://repo.bodar.com/com.googlecode.yadic:yadic:jar:116"));
        File[] files = directory.listFiles();
        assertThat(files.length, is(1));
        assertThat(files[0].getName(), is("yadic-116.jar"));
    }

    @Test
    public void supportsUnspecifiedMavenRepository() throws Exception{
        File directory = temporaryDirectory();
        Resolver resolver = new Resolver(directory);

        assertThat(directory.listFiles().length, is(0));
        resolver.resolve(new URI("mvn:org.objenesis:objenesis:jar:1.2"));
        File[] files = directory.listFiles();
        assertThat(files.length, is(1));
        assertThat(files[0].getName(), is("objenesis-1.2.jar"));
    }
}
