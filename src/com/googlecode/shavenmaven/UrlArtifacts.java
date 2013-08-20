package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Sequence;

import static com.googlecode.totallylazy.Sequences.sequence;

public enum UrlArtifacts implements Artifacts { instance ;
    @Override
    public String scheme() {
        return "*";
    }

    @Override
    public Sequence<UrlArtifact> parse(String value) {
        return sequence(new UrlArtifact(value));
    }
}
