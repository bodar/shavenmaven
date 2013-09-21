package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;

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

    public Uri value() {
        return Uri.uri(value);
    }

    private String filesuffix() {
        if (type.equals("jar")) return ".jar";
        if (type.equals("pack")) return ".pack.gz";
        if (type.endsWith("-pack")) return "-" + type.replace("-pack", ".pack.gz");
        return format("-%s.jar", type);
    }

    public Uri uri() {
        return Uri.uri(repository + path());
    }

    @Override
    public Request request() {
        return RequestBuilder.get(uri()).build();
    }

    private String path() {
        return String.format("%s/%s/%s/%s",
                replaceDots(group()),
                id(),
                version(),
                remoteFilename());
    }

    public String filename() {
        return remoteFilename().replace(".pack.gz", ".jar");
    }

    public String remoteFilename() {
        return format("%s-%s%s", id(), version(), filesuffix());
    }

    @Override
    public String toString() {
        return format("%s (%s)", uri(), value);
    }
}
