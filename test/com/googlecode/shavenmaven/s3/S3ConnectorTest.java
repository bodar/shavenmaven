package com.googlecode.shavenmaven.s3;

import com.googlecode.totallylazy.time.StoppedClock;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import org.junit.Test;

import java.util.Date;

import static com.googlecode.shavenmaven.Artifacts.constructors.artifact;
import static com.googlecode.shavenmaven.s3.AwsCredentials.awsCredentials;
import static com.googlecode.totallylazy.time.Dates.date;
import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static com.googlecode.utterlyidle.HttpHeaders.DATE;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class S3ConnectorTest {
    @Test
    public void createsAwsAuthorisedHttpConnection() throws Exception {
        Date now = date(2001, 1, 1);
        S3Connector connector = new S3Connector(awsCredentials("*", "access-key", "secret-key"), new StoppedClock(now));
        Request request = connector.call(artifact("s3://repo.bodar.com/com.googlecode.yadic:yadic:jar:116"));
        assertThat(request.uri().toString(), is("http://repo.bodar.com.s3.amazonaws.com/com/googlecode/yadic/yadic/116/yadic-116.jar"));
        assertThat(request.headers().getValue(AUTHORIZATION), is("AWS access-key:P/meDoCaNWNOXBlnWPSqPjO+1rM="));
    }

    @Test // http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e4014
    public void exampleObjectGet() throws Exception {
        Request request = get("http://johnsmith.s3.amazonaws.com/photos/puppy.jpg").header(DATE, "Tue, 27 Mar 2007 19:36:42 +0000").build();
        S3Connector connector = new S3Connector(awsCredentials("*", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"), null);
        Request signed = connector.sign(request);
        assertThat(signed.headers().getValue(AUTHORIZATION), is("AWS AKIAIOSFODNN7EXAMPLE:bWq2s1WEIj+Ydj0vQ697zp+IXMU="));
    }

}
