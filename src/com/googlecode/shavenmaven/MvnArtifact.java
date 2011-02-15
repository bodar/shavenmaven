package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.regex.Regex;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.regex.MatchResult;

import static java.lang.String.format;

public class MvnArtifact implements Artifact {
    public static final String PROTOCOL = "mvn";
    private static Regex regex = Regex.regex("mvn:(//[^/]+/)?([^:]+):([^:]+):(\\w+):([\\d\\w\\.]+)");
    private final String repository;
    private final String group;
    private final String id;
    private final String version;
    private final String type;
    private final URI uri;

    public MvnArtifact(URI uri) {
        String value = uri.toString();
        if(!regex.matches(value)){
            throw new IllegalArgumentException("Can only parse mvn: urls " + value);
        }
        this.uri = uri;
        MatchResult match = regex.findMatches(value).head();
        this.repository = repository(match.group(1));
        this.group = match.group(2);
        this.id =  match.group(3);
        this.type =  match.group(4);
        this.version =  match.group(5);
    }

    private static String repository(String host) {
        return host == null ? "http://repo1.maven.org/maven2/" : "http:" + host;
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

    public URL url() {
        try {
            return new URL(repository + path());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private String path() {
        return String.format("%s/%s/%s/%s",
                replaceDots(group()),
                id(),
                version(),
                filename());
    }

    public String filename() {
        return format("%s-%s.%s", id(), version(), type());
    }

    @Override
    public String toString() {
        return format("%s (%s)", url(), uri);
    }
}
