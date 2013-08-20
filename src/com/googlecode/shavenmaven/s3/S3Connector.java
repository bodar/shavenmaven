package com.googlecode.shavenmaven.s3;

import com.googlecode.shavenmaven.Artifact;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.totallylazy.time.SystemClock;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.internal.codec.binary.$Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static java.lang.String.format;

public class S3Connector implements Callable1<Artifact, Request> {
    private final AwsCredentials awsCredentials;
    private final Clock clock;

    public S3Connector(final AwsCredentials awsCredentials, Clock clock) {
        this.awsCredentials = awsCredentials;
        this.clock = clock;
    }

    public static S3Connector s3ConnectionUsing(final AwsCredentials awsCredentials) {
        return new S3Connector(awsCredentials, new SystemClock());
    }

    @Override
    public Request call(Artifact artifact) throws Exception {
        Date now = clock.now();
        return sign(get(artifact.uri()).
                header(HttpHeaders.DATE, Dates.RFC822().format(now)).
                header(HttpHeaders.CONTENT_LENGTH, 0).
                build());
    }

    private Mac mac() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] keyBytes = awsCredentials.secretKey().getBytes("UTF8");
        final SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
        final Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        return mac;
    }


    String s3 = "s3.amazonaws.com";

    private String moveBucketToPath(Uri artifactUri) {
        return "/" + artifactUri.authority().split("." + s3)[0] + artifactUri.path();
    }

    public String sign(String data) throws Exception {
        System.out.println("data = " + data);
        return $Base64.encodeBase64String(mac().doFinal(data.getBytes("UTF8"))).trim();
    }

    public Request sign(Request request) throws Exception {
        String auth = sign(format("%s\n\n\n%s\n%s", request.method(), request.headers().getValue(HttpHeaders.DATE), moveBucketToPath(request.uri())));
        return modify(request).header(HttpHeaders.AUTHORIZATION, format("AWS %s:%s", awsCredentials.accessKeyId(), auth)).build();
    }
}
