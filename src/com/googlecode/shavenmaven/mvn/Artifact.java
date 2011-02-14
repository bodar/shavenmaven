package com.googlecode.shavenmaven.mvn;

import java.net.MalformedURLException;
import java.net.URL;

import static com.googlecode.totallylazy.regex.Regex.regex;
import static java.lang.String.format;

public class Artifact {
    public static final String PROTOCOL = "mvn";
    private final String repository;
    private final String group;
    private final String id;
    private final String version;
    private final String type;

    public Artifact(String repository, String group, String id, String version, String type) {
        this.repository = repository;
        this.group = group;
        this.id = id;
        this.version = version;
        this.type = type;
    }

    public static Artifact parse(URL url) {
        if(!url.getProtocol().equals(PROTOCOL)){
            throw new IllegalArgumentException("Can only parse mvn: urls");
        }
        String[] parts = url.getPath().split(":");
        String groupId = parts[0];
        String artifactId = parts[1];
        String type = parts[2];
        String version = parts[3];

        return new Artifact(repository(url.getHost()), groupId, artifactId, version, type);
    }

    private static String repository(String host) {
        return host.equals("") ? "http://repo1.maven.org/maven2/" : "http://" + host;
    }


    public static String replaceDots(String value) {
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

    public URL url() throws MalformedURLException {
        return new URL(repository + path());
    }

    private String path() {
        return String.format("/%s/%s/%s/%s",
                replaceDots(group()),
                id(),
                version(),
                filename());
    }

    public String filename() {
        return format("%s-%s.%s", id(), version(), type());
    }

}
