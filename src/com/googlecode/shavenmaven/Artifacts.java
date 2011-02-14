package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Predicate;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static com.googlecode.totallylazy.Bytes.bytes;
import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Files.name;
import static com.googlecode.totallylazy.Files.write;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.where;

public class Artifacts {
    public static Artifact artifact(String value) {
        URI uri = URI.create(value);
        if(uri.getScheme().equals(MvnArtifact.PROTOCOL)){
            return new MvnArtifact(uri);
        }
        return new UrlArtifact(uri);
        
    }

    public static Callable1<? super String, Artifact> asArtifact() {
        return new Callable1<String, Artifact>() {
            public Artifact call(String value) throws Exception {
                return Artifacts.artifact(value);
            }
        };
    }

    public static Callable1<? super Artifact, String> asFilename() {
        return new Callable1<Artifact, String>() {
            public String call(Artifact uri) throws Exception {
                return uri.filename();
            }
        };
    }
    
    public static Predicate<? super Artifact> existsIn(final File directory) {
        return new Predicate<Artifact>() {
            public boolean matches(Artifact artifact) {
                return files(directory).exists(where(name(), is(artifact.filename())));
            }
        };
    }

    public static void writeTo(Artifact artifact, File directory) {
        try {
            write(bytes(artifact.url().openStream()), new File(directory, artifact.filename()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}
