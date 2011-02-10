package com.googlecode.shavenmaven;

import java.io.File;
import java.io.IOException;

import static java.util.UUID.randomUUID;

public class TemporaryDirectory extends File {
    public TemporaryDirectory() throws IOException {
        super(System.getProperty("java.io.tmpdir"), randomUUID().toString());
        if (!mkdir()) {
            throw new IOException("Could not create temp directory " + this.getAbsolutePath());
        }
        deleteOnExit();
    }
}
