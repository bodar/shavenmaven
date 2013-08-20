package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Uri;

import static java.lang.String.format;

public class MvnArtifact implements Artifact {
    private final String repository;
    private final String group;
    private final String id;
    private final String version;
    private final String type;
    private final String value;

    MvnArtifact(String repository, String group, String id, String version, String type, String value) {
        this.repository = repository;
        this.group = group;
        this.id = id;
        this.version = version;
        this.type = type;
        this.value = value;
    }

    private static String replaceDots(String value) {
        return value.replace('.', '/');
    }

    public String group() {
        return group;
    }

    public String id() {
        return id;
    }

    public String version() {
        return version;
    }

    public String type() {
        return type;
    }

    public String value() {
        return value;
    }

    private String filesuffix() {
        return "jar".equals(type) ? ".jar" : format("-%s.jar", type);
    }

    public Uri uri() {
        return Uri.uri(repository + path());
    }

    private String path() {
        return String.format("%s/%s/%s/%s",
                replaceDots(group()),
                id(),
                version(),
                filename());
    }

    public String filename() {
        return format("%s-%s%s", id(), version(), filesuffix());
    }

    @Override
    public String toString() {
        return format("%s (%s)", uri(), value);
    }
}
