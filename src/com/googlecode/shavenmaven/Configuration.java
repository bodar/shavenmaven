package com.googlecode.shavenmaven;

import java.io.PrintStream;

import com.googlecode.totallylazy.Sequence;

import static com.googlecode.totallylazy.Streams.nullPrintStream;

public class Configuration {

    private PrintStream out;

    private Configuration() {
        this.out = System.out;
    }

    public PrintStream out() {
        return out;
    }

    public static Configuration parse(final Sequence<String> options, final Procedure usage) {
        final Configuration config = new Configuration();
        options.each(option -> {
            if (option.equals("-q") || option.equals("--quiet")) {
                config.quiet();
            } else {
                System.err.println("unknown option: " + option);
                usage.execute();
            }
        });
        return config;
    }

    private void quiet() {
        out = nullPrintStream();
    }

    @FunctionalInterface
    interface Procedure {
        void execute();
    }
}
