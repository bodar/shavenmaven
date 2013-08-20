package com.googlecode.shavenmaven.s3;

import com.googlecode.utterlyidle.Request;
import org.junit.Test;

import static com.googlecode.shavenmaven.s3.AwsCredentials.awsCredentials;
import static com.googlecode.utterlyidle.HttpHeaders.AUTHORIZATION;
import static com.googlecode.utterlyidle.HttpHeaders.DATE;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class S3SignerTest {
    @Test // http://docs.aws.amazon.com/AmazonS3/latest/dev/RESTAuthentication.html#d0e4014
    public void exampleObjectGet() throws Exception {
        Request request = get("http://johnsmith.s3.amazonaws.com/photos/puppy.jpg").header(DATE, "Tue, 27 Mar 2007 19:36:42 +0000").build();
        S3Signer connector = new S3Signer(awsCredentials("*", "AKIAIOSFODNN7EXAMPLE", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"), null);
        Request signed = connector.sign(request);
        assertThat(signed.headers().getValue(AUTHORIZATION), is("AWS AKIAIOSFODNN7EXAMPLE:bWq2s1WEIj+Ydj0vQ697zp+IXMU="));
    }
}
