package com.googlecode.shavenmaven.mvn;

import com.googlecode.totallylazy.regex.Matches;
import com.googlecode.totallylazy.regex.Regex;

import java.net.URL;

import static com.googlecode.totallylazy.regex.Regex.regex;

public class Artifact {
    private static final Regex regex = regex("([^/]+)/([^/]+)/([^/]+)/([^/]+)");
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
        if(!url.getProtocol().equals(Handler.PROTOCOL)){
            throw new IllegalArgumentException("Can only parse mvn: urls");
        }
        Matches matches = regex.findMatches(url.getPath());
        String groupId = matches.head().group(1);
        String artifactId = matches.head().group(2);
        String version = matches.head().group(3);
        String type = matches.head().group(4);

        return new Artifact(url.getHost(), groupId, artifactId, version, type);
    }

    public String repository() {
        return repository;
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
}
