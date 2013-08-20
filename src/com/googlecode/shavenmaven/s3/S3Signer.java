package com.googlecode.shavenmaven.s3;

import com.googlecode.totallylazy.Function;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.internal.codec.binary.$Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.googlecode.utterlyidle.RequestBuilder.modify;
import static java.lang.String.format;

public class S3Signer {
    private final AwsCredentials awsCredentials;
    private final Clock clock;

    public S3Signer(final AwsCredentials awsCredentials, Clock clock) {
        this.awsCredentials = awsCredentials;
        this.clock = clock;
    }

    public String sign(String data) throws Exception {
        return $Base64.encodeBase64String(mac().doFinal(data.getBytes("UTF8"))).trim();
    }

    public Request sign(Request request) throws Exception {
        String auth = sign(format("%s\n\n\n%s\n%s", request.method(), date(request), moveBucketToPath(request.uri())));
        return modify(request).header(HttpHeaders.AUTHORIZATION, format("AWS %s:%s", awsCredentials.accessKeyId(), auth)).build();
    }

    private String date(Request request) {
        return request.headers().valueOption(HttpHeaders.DATE).getOrElse(new Function<String>() {
            @Override
            public String call() throws Exception {
                return Dates.RFC822().format(clock.now());
            }
        });
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
}
