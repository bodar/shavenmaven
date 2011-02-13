package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

import static com.googlecode.shavenmaven.Resolver.filename;
import static com.googlecode.shavenmaven.Resolver.url;
import static com.googlecode.totallylazy.Callers.callConcurrently;
import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Files.name;
import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.lines;

public class Dependencies {
    private final Sequence<URL> urls;

    public Dependencies(Iterable<URL> urls) { 
        this.urls = sequence(urls);
    }

    public static Dependencies load(File file) throws IOException {
        return new Dependencies(lines(file).filter(not(empty())).map(asUrl()).memorise());
    }

    private static Predicate<? super String> empty() {
        return new Predicate<String>() {
            public boolean matches(String value) {
                return value == null || value.equals("");
            }
        };
    }

    private static Callable1<? super String, URL> asUrl() {
        return new Callable1<String, URL>() {
            public URL call(String value) throws Exception {
                return url(value);
            }
        };
    }

    public Dependencies update(File directory) {
        files(directory).filter(where(name(), is(not(in(urls.map(asFilename())))))).map(delete()).realise();
        final Resolver resolver = new Resolver(directory);
        try {
            callConcurrently(urls.filter(not(existsIn(directory))).map(resolve(resolver)));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private Callable1<? super File, Boolean> delete() {
        return new Callable1<File, Boolean>() {
            public Boolean call(File file) throws Exception {
                return file.delete();
            }
        };
    }

    private Callable1<? super URL, String> asFilename() {
        return new Callable1<URL, String>() {
            public String call(URL url) throws Exception {
                return filename(url);
            }
        };
    }

    private Callable1<URL, Callable<Resolver>> resolve(final Resolver resolver) {
        return new Callable1<URL, Callable<Resolver>>() {
            public Callable<Resolver> call(final URL url) throws Exception {
                return new Callable<Resolver>() {
                    public Resolver call() throws Exception {
                        return resolver.resolve(url);
                    }
                };
            }
        };
    }

    private Predicate<? super URL> existsIn(final File directory) {
        return new Predicate<URL>() {
            public boolean matches(URL url) {
                return files(directory).exists(where(name(), is(filename(url))));
            }
        };
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
