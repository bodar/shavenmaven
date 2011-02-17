package com.googlecode.shavenmaven;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static com.googlecode.totallylazy.Sequences.sequence;

public class UrlArtifact implements Artifact{
    private final URI uri;

    public UrlArtifact(URI uri) {
        this.uri = uri;
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
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String filename() {
        return sequence(url().getPath().split("/")).reverse().head();
    }

    @Override
    public String toString() {
        return uri.toString();
    }
}
