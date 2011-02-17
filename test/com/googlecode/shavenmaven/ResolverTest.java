package com.googlecode.shavenmaven;

import org.junit.Test;

import java.io.File;

import static com.googlecode.shavenmaven.Artifacts.artifact;
import static com.googlecode.shavenmaven.DependenciesTest.dependencyUrl;
import static com.googlecode.totallylazy.Files.temporaryDirectory;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResolverTest {
    @Test
    public void resolvesArtifact() throws Exception{
        File directory = temporaryDirectory();
        Resolver resolver = new Resolver(directory);

        assertThat(directory.listFiles().length, is(0));
        resolver.resolve(artifact(dependencyUrl()));
        File[] files = directory.listFiles();
        assertThat(files.length, is(1));
        assertThat(files[0].getName(), is("dependency.txt"));
    }
}
