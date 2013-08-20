package com.googlecode.shavenmaven.s3;

import com.googlecode.totallylazy.Uri;
import org.junit.Test;

import static com.googlecode.shavenmaven.s3.AwsCredentials.awsCredentials;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AwsCredentialsTest {
    @Test
    public void matchesArtifactsWithSpecifiedPrefix() {
        final AwsCredentials awsCredentials = awsCredentials("s3://repo.bodar.com", "key", "secret");
        assertThat(awsCredentials.matches(Uri.uri("s3://repo.bodar.com/com.googlecode.yadic:yadic:jar:116")), is(true));
        assertThat(awsCredentials.matches(Uri.uri("s3://otherrepo.bodar.com/com.googlecode.yadic:yadic:jar:116")), is(false));
    }
}