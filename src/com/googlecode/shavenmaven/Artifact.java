package com.googlecode.shavenmaven;

import java.net.URL;

public interface Artifact {
    String group();

    String id();

    String version();

    String type();

    URL url();

    String filename();
}
