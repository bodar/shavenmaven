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
        S3Artifact s3Artifact = sequence(parse("s3://repo.bodar.com/com.googlecode.yadic:yadic:jar:116")).head();
        assertThat(s3Artifact.group(), is("com.googlecode.yadic"));
        assertThat(s3Artifact.id(), is("yadic"));
        assertThat(s3Artifact.type(), is("jar"));
        assertThat(s3Artifact.version(), is("116"));
        assertThat(s3Artifact.uri().toString(), is(new URL("http://repo.bodar.com.s3.amazonaws.com/com/googlecode/yadic/yadic/116/yadic-116.jar").toString()));
        assertThat(s3Artifact.filename(), is("yadic-116.jar"));
        assertThat(s3Artifact.toString(), is("http://repo.bodar.com.s3.amazonaws.com/com/googlecode/yadic/yadic/116/yadic-116.jar (s3://repo.bodar.com/com.googlecode.yadic:yadic:jar:116)"));
    }

}
