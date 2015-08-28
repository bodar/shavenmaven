package com.googlecode.shavenmaven.s3;

import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.io.Uri;

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

    @Override
    public boolean matches(Uri artifact) {
        if (pattern.equals(ANY)) return true;
        return artifact.toString().startsWith(pattern);
    }

    public String pattern() {
        return pattern;
    }
}
