package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;

import javax.annotation.processing.FilerException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

import static com.googlecode.shavenmaven.Resolver.url;
import static com.googlecode.totallylazy.Callers.callConcurrently;
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
        final Resolver resolver = new Resolver(directory);
        try {
            callConcurrently(urls.map(new Callable1<URL, Callable<Resolver>>() {
                public Callable<Resolver> call(final URL url) throws Exception {
                    return new Callable<Resolver>() {
                        public Resolver call() throws Exception {
                            return resolver.resolve(url);
                        }
                    };
                }
            }));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public static void main(String[] args) throws IOException {
        if(args.length == 0 || args.length > 2){
            System.err.println("usage: dependencies.file [directory]");
            System.exit(-1);
        }
        Sequence<String> arguments = sequence(args);
        load(dependenciesFile(arguments.head())).update(destinationDirectory(arguments.tail()));
    }

    private static File destinationDirectory(Sequence<String> arg) {
        return arg.map(asFile()).add(new File(System.getProperty("user.dir"))).head();
    }

    private static Callable1<? super String,File> asFile() {
        return new Callable1<String, File>() {
            public File call(String name) throws Exception {
                return new File(name);
            }
        };
    }

    private static File dependenciesFile(String arg) {
        return new File(arg);
    }

}
