package com.googlecode.shavenmaven.s3;

import com.googlecode.shavenmaven.Artifact;
import com.googlecode.totallylazy.Predicate;

import java.util.Properties;

public class AwsCredentials implements Predicate<Artifact> {

    private final String pattern;
    private final String accessKey;
    private final String secretKey;

    public AwsCredentials(String pattern, String accessKey, String secretKey) {

        this.pattern = pattern;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public static AwsCredentials awsCredentials(String pattern, final String accessKey, final String secretKey) {
        return new AwsCredentials(pattern, accessKey, secretKey);
    }

    @Override
    public boolean matches(Artifact artifact) {
        return artifact.value().startsWith(pattern);
    }

    public String pattern() {
        return pattern;
    }

    public String accessKeyId() {
        return accessKey;
    }

    public String secretKey() {
        return secretKey;
    }
}
