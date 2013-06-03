package com.googlecode.shavenmaven;

import java.net.MalformedURLException;
import java.net.URL;

import static com.googlecode.totallylazy.LazyException.lazyException;
import static com.googlecode.totallylazy.Sequences.sequence;

public class UrlArtifact implements Artifact {
    private final String value;

    public UrlArtifact(String value) {
        this.value = value;
    }

    public static UrlArtifact parse(String value) {
        return new UrlArtifact(value);
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

    public URL url() {
        try {
            return new URL(value);
        } catch (MalformedURLException e) {
            throw lazyException(e);
        }
    }

    public String value() {
        return value;
    }

    public String filename() {
        return sequence(url().getPath().split("/")).reverse().head();
    }

    @Override
    public String toString() {
        return value;
    }
}
