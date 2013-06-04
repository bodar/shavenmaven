package com.googlecode.shavenmaven.s3;

import org.junit.Test;

import java.net.HttpURLConnection;

import static com.googlecode.shavenmaven.Artifacts.artifact;
import static com.googlecode.shavenmaven.s3.AwsCredentials.awsCredentials;
import static com.googlecode.totallylazy.Sequences.sequence;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class S3ConnectorTest {

    @Test
    public void createsAwsAuthorisedHttpConnection() throws Exception {
        final S3Connector connector = new S3Connector(awsCredentials("*", "access-key", "secret-key"));
        final HttpURLConnection connection = (HttpURLConnection) connector.call(sequence(artifact("s3://repo.bodar.com/com.googlecode.yadic:yadic:jar:116")).head());
        assertThat(connection.getURL().toString(), is("http://s3.amazonaws.com:80/repo.bodar.com/com/googlecode/yadic/yadic/116/yadic-116.jar"));
    }
}
