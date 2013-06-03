package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Rule;
import com.googlecode.totallylazy.Rules;

import java.io.File;
import java.net.URLConnection;

import static com.googlecode.totallylazy.Predicates.always;
import static com.googlecode.totallylazy.Rule.rule;
import static com.googlecode.totallylazy.Sequences.sequence;

public class ConnectionRules {

    public static Rules<Artifact, URLConnection> connectByUrlRules() {
        final Rule<Artifact, URLConnection> rule = rule(always(Artifact.class), connectByUrl());
        return Rules.<Artifact, URLConnection>rules(sequence(rule));
    }

    private static Function1<Artifact, URLConnection> connectByUrl() {
        return new Function1<Artifact, URLConnection>() {
            @Override
            public URLConnection call(Artifact artifact) throws Exception {
                return artifact.url().openConnection();
            }
        };
    }

    public static Rule<Artifact, URLConnection> authenticatedS3ConnectionRule(final File buildDirectory) {
        return rule(always(Artifact.class), connectByUrl());
    }
}
