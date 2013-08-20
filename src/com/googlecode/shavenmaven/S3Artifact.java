package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Request;

import static java.lang.String.format;

public class S3Artifact extends DelegatingArtifact<MvnArtifact> {
    private final Uri value;
    private final Request request;

    protected S3Artifact(Uri value, MvnArtifact artifact, Request request) {
        super(artifact);
        this.value = value;
        this.request = request;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public String toString() {
        return format("%s (%s)", uri(), value());
    }

    public Uri value() {
        return value;
    }
}
