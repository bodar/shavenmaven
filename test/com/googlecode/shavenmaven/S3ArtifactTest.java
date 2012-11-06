package com.googlecode.shavenmaven;

import org.junit.Test;

import java.net.URL;

import static com.googlecode.shavenmaven.S3Artifact.parse;
import static com.googlecode.totallylazy.Sequences.sequence;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class S3ArtifactTest {
    @Test
    public void supportsUriWithExplicitRepositoryAndRootFolder() throws Exception {
        MvnArtifact mvnArtifact = sequence(parse("s3://repo.bodar.com/com.googlecode.yadic:yadic:jar:116")).head();
        assertThat(mvnArtifact.group(), is("com.googlecode.yadic"));
        assertThat(mvnArtifact.id(), is("yadic"));
        assertThat(mvnArtifact.type(), is("jar"));
        assertThat(mvnArtifact.version(), is("116"));
        assertThat(mvnArtifact.url().toString(), is(new URL("http://repo.bodar.com.s3.amazonaws.com/com/googlecode/yadic/yadic/116/yadic-116.jar").toString()));
        assertThat(mvnArtifact.filename(), is("yadic-116.jar"));
    }

}
