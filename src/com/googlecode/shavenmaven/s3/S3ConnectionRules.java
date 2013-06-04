package com.googlecode.shavenmaven.s3;

import com.googlecode.shavenmaven.Artifact;
import com.googlecode.shavenmaven.S3Artifact;
import com.googlecode.totallylazy.*;

import java.net.URLConnection;

import static com.googlecode.shavenmaven.ConnectionRules.alwaysConnectByUrl;
import static com.googlecode.shavenmaven.s3.S3Connector.s3ConnectionUsing;
import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Rule.rule;

public class S3ConnectionRules {

    public static Rule<Artifact, URLConnection> authenticatedS3ConnectionRule(Sequence<AwsCredentials> awsCredentials) throws Exception {
        return rule(instanceOf(S3Artifact.class), s3ConnectionRules(awsCredentials));
    }

    private static Callable1<Artifact,URLConnection> s3ConnectionRules(Sequence<AwsCredentials> awsCredentials) throws Exception {
        return awsCredentials.map(toConnectionRule()).fold(Rules.<Artifact, URLConnection>rules(), merge()).addLast(alwaysConnectByUrl());
    }

    private static Callable2<Rules<Artifact, URLConnection>, Rule<Artifact, URLConnection>, Rules<Artifact, URLConnection>> merge() {
        return new Function2<Rules<Artifact, URLConnection>, Rule<Artifact, URLConnection>, Rules<Artifact, URLConnection>>() {
            @Override
            public Rules<Artifact, URLConnection> call(Rules<Artifact, URLConnection> rules, Rule<Artifact, URLConnection> rule) throws Exception {
                return rules.addLast(rule);
            }
        };
    }

    private static Callable1<AwsCredentials, Rule<Artifact, URLConnection>> toConnectionRule() {
        return new Callable1<AwsCredentials, Rule<Artifact, URLConnection>>() {
            @Override
            public Rule<Artifact, URLConnection> call(AwsCredentials awsCredentials) throws Exception {
                return Rule.rule(awsCredentials, s3ConnectionUsing(awsCredentials));
            }
        };
    }
}
