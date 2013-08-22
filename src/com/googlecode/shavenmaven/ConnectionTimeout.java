package com.googlecode.shavenmaven;

import static java.lang.Integer.valueOf;

public class ConnectionTimeout {
    public static final String KEY = "shavenmaven.connection.timeout";

    public static int connectionTimeout() {
        return valueOf(System.getProperty(KEY, "0"));
    }

    public static int connectionTimeout(int value) {
        return valueOf(System.setProperty(KEY, String.valueOf(value)));
    }

}
