package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Rule;
import com.googlecode.totallylazy.Rules;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;

import static com.googlecode.totallylazy.Predicates.always;
import static com.googlecode.totallylazy.Rule.rule;
import static com.googlecode.totallylazy.Sequences.sequence;

public class ConnectionRules {

    public static Rules<Artifact, Request> connectByUrlRules() {
        final Rule<Artifact, Request> rule = alwaysConnectByUrl();
        return Rules.<Artifact, Request>rules(sequence(rule));
    }

    public static Rule<Artifact, Request> alwaysConnectByUrl() {
        return rule(always(Artifact.class), connectByUrl());
    }

    private static Function1<Artifact, Request> connectByUrl() {
        return new Function1<Artifact, Request>() {
            @Override
            public Request call(Artifact artifact) throws Exception {
                return RequestBuilder.get(artifact.uri()).build();
            }
        };
    }
}
