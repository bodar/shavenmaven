package com.googlecode.shavenmaven.s3;

import com.googlecode.totallylazy.io.Uri;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;

import static com.googlecode.utterlyidle.Request.Builder.header;
import static com.googlecode.utterlyidle.Request.Builder.modify;
import static java.lang.String.format;

public class S3Signer extends com.googlecode.utterlyidle.s3.S3Signer {
    private final AwsCredentials awsCredentials;
    private final Clock clock;

    public S3Signer(final AwsCredentials awsCredentials, Clock clock) {
        this.awsCredentials = awsCredentials;
        this.clock = clock;
    }

    public Request sign(Request request) throws Exception {
        String date = date(request);
        String auth = format("%s\n\n\n%s\n%s", request.method(), date, moveBucketToPath(request.uri()));
        return modify(request, header(HttpHeaders.DATE, date), header(HttpHeaders.AUTHORIZATION, authorizationHeader(awsCredentials, auth)));
    }

    private String date(Request request) {
        return request.headers().valueOption(HttpHeaders.DATE).getOrElse(() -> {
            return Dates.RFC822().format(clock.now());
        });
    }

    String s3 = "s3.amazonaws.com";

    private String moveBucketToPath(Uri artifactUri) {
        return "/" + artifactUri.authority().split("." + s3)[0] + artifactUri.path();
    }
}
