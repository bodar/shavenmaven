package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;

import static com.googlecode.totallylazy.Sequences.sequence;

public class UrlArtifact implements Artifact {
    private final String value;

    public UrlArtifact(String value) {
        this.value = value;
    }

    public String group() {
        throw new UnsupportedOperationException();
    }

    public String id() {
        throw new UnsupportedOperationException();
    }

    public String version() {
        throw new UnsupportedOperationException();
    }

    public String type() {
        throw new UnsupportedOperationException();
    }

    public Uri uri() {
        return Uri.uri(value);
    }

    @Override
    public Request request() {
        return RequestBuilder.get(uri()).build();
    }

    public Uri value() {
        return uri();
    }

    public String filename() {
        return sequence(uri().path().split("/")).reverse().head();
    }

    @Override
    public String toString() {
        return value;
    }
}
