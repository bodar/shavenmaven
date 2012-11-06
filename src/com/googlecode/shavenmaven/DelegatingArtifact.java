package com.googlecode.shavenmaven;

import java.net.URL;

public abstract class DelegatingArtifact implements Artifact {
    protected final Artifact artifact;

    protected DelegatingArtifact(Artifact artifact) {
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
    public URL url() {return artifact.url();}

    @Override
    public String filename() {return artifact.filename();}

    @Override
    public int hashCode() { return artifact.hashCode(); }

    @Override
    public String toString() { return artifact.toString(); }

    @Override
    public boolean equals(Object obj) { return artifact.equals(obj); }
}