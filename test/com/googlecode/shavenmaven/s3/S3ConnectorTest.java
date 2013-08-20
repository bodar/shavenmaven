package com.googlecode.shavenmaven.s3;

import com.googlecode.totallylazy.time.StoppedClock;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import org.junit.Test;

import java.util.Date;

import static com.googlecode.shavenmaven.Artifacts.constructors.artifact;
import static com.googlecode.shavenmaven.s3.AwsCredentials.awsCredentials;
import static com.googlecode.totallylazy.time.Dates.date;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class S3ConnectorTest {

    @Test
    public void createsAwsAuthorisedHttpConnection() throws Exception {
        Date now = date(2001, 1, 1);
        S3Connector connector = new S3Connector(awsCredentials("*", "access-key", "secret-key"), new StoppedClock(now));
        Request request = connector.call(artifact("s3://repo.bodar.com/com.googlecode.yadic:yadic:jar:116"));
        assertThat(request.uri().toString(), is("http://s3.amazonaws.com/repo.bodar.com/com/googlecode/yadic/yadic/116/yadic-116.jar"));
        assertThat(request.headers().getValue(HttpHeaders.AUTHORIZATION), is("AWS access-key:P/meDoCaNWNOXBlnWPSqPjO+1rM="));
    }
}
