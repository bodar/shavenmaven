package com.googlecode.shavenmaven.mvn;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import static com.googlecode.shavenmaven.MvnArtifact.parse;
import static com.googlecode.totallylazy.regex.Regex.regex;
import static java.lang.String.format;

public class Handler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return parse(url).url().openConnection();
    }

}
