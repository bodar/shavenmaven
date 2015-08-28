package com.googlecode.shavenmaven.s3;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.io.Uri;

import static com.googlecode.totallylazy.Option.option;

public class AwsCredentials extends com.googlecode.utterlyidle.s3.AwsCredentials implements Predicate<Uri> {
    public static final String ANY = "*";
    private final String pattern;

    public AwsCredentials(String pattern, String accessKey, String secretKey) {
        super(accessKey, secretKey);
        this.pattern = pattern;
    }

    public static AwsCredentials awsCredentials(String pattern, final String accessKey, final String secretKey) {
        return new AwsCredentials(pattern, accessKey, secretKey);
    }

    public static AwsCredentials awsCredentials(final String accessKey, final String secretKey) {
        return awsCredentials(ANY, accessKey, secretKey);
    }

    public static Option<AwsCredentials> environmentCredentials() {
        return option(() -> {
            com.googlecode.utterlyidle.s3.AwsCredentials credentials = com.googlecode.utterlyidle.s3.AwsCredentials.awsCredentials();
            return awsCredentials(ANY, credentials.accessKeyId(), credentials.secretKey());
        });
    }

    @Override
    public boolean matches(Uri artifact) {
        if (pattern.equals(ANY)) return true;
        return artifact.toString().startsWith(pattern);
    }

    public String pattern() {
        return pattern;
    }
}
