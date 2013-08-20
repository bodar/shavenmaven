package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Uri;

import java.net.URL;

public abstract class DelegatingArtifact<T extends Artifact> implements Artifact {
    protected final T artifact;

    protected DelegatingArtifact(T artifact) {
        this.artifact = artifact;
    }

    @Override
    public String group() {return artifact.group();}

    @Override
    public String id() {return artifact.id();}

    @Override
    public String version() {return artifact.version();}

    @Override
    public String type() {return artifact.type();}

    @Override
    public Uri uri() {return artifact.uri();}

    @Override
    public String filename() {return artifact.filename();}

    @Override
    public String value() {return artifact.value();}

    @Override
    public int hashCode() { return artifact.hashCode(); }

    @Override
    public String toString() { return artifact.toString(); }

    @Override
    public boolean equals(Object obj) { return artifact.equals(obj); }
}
