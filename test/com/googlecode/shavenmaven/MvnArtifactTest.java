package com.googlecode.shavenmaven;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.net.URI;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MvnArtifactTest {
    @Test
    public void supportsUriWithNoExplicitRepository() throws Exception {
        MvnArtifact mvnArtifact = new MvnArtifact(URI.create("mvn:org.objenesis:objenesis:jar:1.2"));
        assertThat(mvnArtifact.group(), is("org.objenesis"));
        assertThat(mvnArtifact.id(), is("objenesis"));
        assertThat(mvnArtifact.type(), is("jar"));
        assertThat(mvnArtifact.version(), is("1.2"));
        assertThat(mvnArtifact.url(), is(new URL("http://repo1.maven.org/maven2/org/objenesis/objenesis/1.2/objenesis-1.2.jar")));
        assertThat(mvnArtifact.filename(), is("objenesis-1.2.jar"));
    }

    @Test
    public void supportsUriWithExplicitRepository() throws Exception {
        MvnArtifact mvnArtifact = new MvnArtifact(URI.create("mvn://repo.bodar.com/com.googlecode.yadic:yadic:jar:116"));
        assertThat(mvnArtifact.group(), is("com.googlecode.yadic"));
        assertThat(mvnArtifact.id(), is("yadic"));
        assertThat(mvnArtifact.type(), is("jar"));
        assertThat(mvnArtifact.version(), is("116"));
        assertThat(mvnArtifact.url(), is(new URL("http://repo.bodar.com/com/googlecode/yadic/yadic/116/yadic-116.jar")));
        assertThat(mvnArtifact.filename(), is("yadic-116.jar"));
    }
}
