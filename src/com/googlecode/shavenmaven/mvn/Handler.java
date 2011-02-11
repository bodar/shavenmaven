package com.googlecode.shavenmaven.mvn;

import com.googlecode.totallylazy.regex.Regex;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import static com.googlecode.shavenmaven.mvn.Artifact.parse;
import static com.googlecode.totallylazy.regex.Regex.regex;
import static java.lang.String.format;

public class Handler extends URLStreamHandler {
    public static final String PROTOCOL = "mvn";

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        Artifact artifact = parse(url);
        return new URL(artifact.repository() + path(artifact)).openConnection();
    }

    public static String path(Artifact artifact) {
        return format("/%s/%s/%s/%s",
                replaceDots(artifact.group()),
                artifact.id(),
                artifact.version(),
                filename(artifact));
    }

    public static String filename(Artifact artifact) {
        return format("%s-%s.%s", artifact.id(), artifact.version(), artifact.type());
    }

    private static String replaceDots(String value) {
        return value.replace('.', '/');
    }
}
