package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.time.StoppedClock;
import com.googlecode.utterlyidle.Request;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.net.URL;
import java.util.Date;

import static com.googlecode.shavenmaven.s3.AwsCredentials.awsCredentials;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.time.Dates.date;
import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class S3ArtifactTest {
    @Test
    public void supportsUriWithExplicitRepositoryAndRootFolder() throws Exception {
        S3Artifact s3Artifact = sequence(S3Artifacts.s3Artifacts().parse("s3://repo.bodar.com/com.googlecode.yadic:yadic:jar:116")).head();
        assertThat(s3Artifact.group(), is("com.googlecode.yadic"));
        assertThat(s3Artifact.id(), is("yadic"));
        assertThat(s3Artifact.type(), is("jar"));
        assertThat(s3Artifact.version(), is("116"));
        assertThat(s3Artifact.value().toString(), is("s3://repo.bodar.com/com.googlecode.yadic:yadic:jar:116"));
        assertThat(s3Artifact.uri().toString(), is(new URL("http://repo.bodar.com.s3.amazonaws.com/com/googlecode/yadic/yadic/116/yadic-116.jar").toString()));
        assertThat(s3Artifact.request().uri().toString(), is(new URL("http://repo.bodar.com.s3.amazonaws.com/com/googlecode/yadic/yadic/116/yadic-116.jar").toString()));
        assertThat(s3Artifact.filename(), is("yadic-116.jar"));
        assertThat(s3Artifact.toString(), is("http://repo.bodar.com.s3.amazonaws.com/com/googlecode/yadic/yadic/116/yadic-116.jar (s3://repo.bodar.com/com.googlecode.yadic:yadic:jar:116)"));
    }

    @Test
    public void supportsAuthorisation() throws Exception {
        Date now = date(2001, 1, 1);
        S3Artifacts s3Artifacts = S3Artifacts.s3Artifacts(new StoppedClock(now), sequence(awsCredentials("*", "access-key", "secret-key")));
        Request request = s3Artifacts.parse("s3://repo.bodar.com/com.googlecode.yadic:yadic:jar:116").head().request();
        assertThat(request.uri().toString(), Matchers.is("http://repo.bodar.com.s3.amazonaws.com/com/googlecode/yadic/yadic/116/yadic-116.jar"));
        assertThat(request.headers().getValue(AUTHORIZATION), Matchers.is("AWS access-key:P/meDoCaNWNOXBlnWPSqPjO+1rM="));
    }
}
