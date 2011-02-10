package com.googlecode.shavenmaven;

import java.io.File;

import static java.util.UUID.randomUUID;

public class TemporaryFile extends File {
    public TemporaryFile() {
        super(System.getProperty("java.io.tmpdir"), randomUUID().toString());
        deleteOnExit();
    }
}
