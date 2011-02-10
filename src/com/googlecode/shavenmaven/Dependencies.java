package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Sequence;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.googlecode.shavenmaven.Resolver.url;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.lines;

public class Dependencies {
    private final Sequence<URL> urls;

    public Dependencies(Iterable<URL> urls) {
        this.urls = sequence(urls);
    }

    public static Dependencies load(File file) throws IOException {
        Sequence<URL> urls = lines(file).map(asUrl());
        return new Dependencies(urls);
    }

    private static Callable1<? super String, URL> asUrl() {
        return new Callable1<String, URL>() {
            public URL call(String value) throws Exception {
                return url(value);
            }
        };
    }

    public Dependencies update(File directory) {
        urls.fold(new Resolver(directory), resolve());
        return this;
    }

    private Callable2<Resolver, URL, Resolver> resolve() {
        return new Callable2<Resolver, URL, Resolver>() {
            public Resolver call(Resolver resolver, URL url) throws Exception {
                return resolver.resolve(url);
            }
        };
    }
}
