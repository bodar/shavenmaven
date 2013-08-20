package com.googlecode.shavenmaven.s3;

import com.googlecode.shavenmaven.Artifact;
import com.googlecode.shavenmaven.S3Artifact;
import com.googlecode.totallylazy.*;
import com.googlecode.utterlyidle.Request;

import static com.googlecode.shavenmaven.ConnectionRules.alwaysConnectByUrl;
import static com.googlecode.shavenmaven.s3.S3Connector.s3ConnectionUsing;
import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Rule.rule;

public class S3ConnectionRules {

    public static Rule<Artifact, Request> authenticatedS3ConnectionRule(Sequence<AwsCredentials> awsCredentials) throws Exception {
        return rule(instanceOf(S3Artifact.class), s3ConnectionRules(awsCredentials));
    }

    private static Callable1<Artifact, Request> s3ConnectionRules(Sequence<AwsCredentials> awsCredentials) throws Exception {
        return awsCredentials.map(toConnectionRule()).fold(Rules.<Artifact, Request>rules(), merge()).addLast(alwaysConnectByUrl());
    }

    private static Callable2<Rules<Artifact, Request>, Rule<Artifact, Request>, Rules<Artifact, Request>> merge() {
        return new Function2<Rules<Artifact, Request>, Rule<Artifact, Request>, Rules<Artifact, Request>>() {
            @Override
            public Rules<Artifact, Request> call(Rules<Artifact, Request> rules, Rule<Artifact, Request> rule) throws Exception {
                return rules.addLast(rule);
            }
        };
    }

    private static Callable1<AwsCredentials, Rule<Artifact, Request>> toConnectionRule() {
        return new Callable1<AwsCredentials, Rule<Artifact, Request>>() {
            @Override
            public Rule<Artifact, Request> call(AwsCredentials awsCredentials) throws Exception {
                return Rule.rule(awsCredentials, s3ConnectionUsing(awsCredentials));
            }
        };
    }
}
