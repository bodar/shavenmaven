package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.*;

import java.net.URLConnection;

import static com.googlecode.totallylazy.Predicates.always;
import static com.googlecode.totallylazy.Rule.rule;
import static com.googlecode.totallylazy.Rules.rules;
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
}
