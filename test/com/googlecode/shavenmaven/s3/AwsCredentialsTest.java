package com.googlecode.shavenmaven.s3;

import org.junit.Test;

import static com.googlecode.shavenmaven.Artifacts.artifact;
import static com.googlecode.shavenmaven.s3.AwsCredentials.awsCredentials;
import static com.googlecode.totallylazy.Sequences.sequence;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AwsCredentialsTest {

    @Test
    public void matchesArtifactsWithSpecifiedPrefix() {
        final AwsCredentials awsCredentials = awsCredentials("s3://repo.bodar.com", "key", "secret");
        assertThat(awsCredentials.matches(sequence(artifact("s3://repo.bodar.com/com.googlecode.yadic:yadic:jar:116")).first()), is(true));
        assertThat(awsCredentials.matches(sequence(artifact("s3://otherrepo.bodar.com/com.googlecode.yadic:yadic:jar:116")).first()), is(false));
    }
}
