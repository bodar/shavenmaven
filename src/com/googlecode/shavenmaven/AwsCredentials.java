package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Predicate;

import java.util.Properties;

public class AwsCredentials implements Predicate<Artifact> {
    private final String pattern;
    private final Properties credentials;

    public AwsCredentials(String pattern, Properties credentials) {

        this.pattern = pattern;
        this.credentials = credentials;
    }

    public static AwsCredentials awsCredentials(String pattern, Properties credentials) {
        return new AwsCredentials(pattern, credentials);
    }

    @Override
    public boolean matches(Artifact artifact) {
        return artifact.url().getAuthority().matches(pattern);
    }

    public String accessKeyId() {
        return credentials.getProperty("AWSAccessKeyId");
    }

    public String secretKey() {
        return credentials.getProperty("AWSSecretKey");
    }
}
