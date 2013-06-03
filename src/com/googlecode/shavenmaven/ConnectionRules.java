package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLConnection;

import static com.googlecode.shavenmaven.AwsCredentials.awsCredentials;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Files.hasSuffix;
import static com.googlecode.totallylazy.Predicates.always;
import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Rule.rule;
import static com.googlecode.totallylazy.Sequences.sequence;

public class ConnectionRules {

    public static Rules<Artifact, URLConnection> connectByUrlRules() {
        final Rule<Artifact, URLConnection> rule = alwaysConnectByUrl();
        return Rules.<Artifact, URLConnection>rules(sequence(rule));
    }

    private static Rule<Artifact, URLConnection> alwaysConnectByUrl() {
        return rule(always(Artifact.class), connectByUrl());
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
        return rule(instanceOf(S3Artifact.class), s3ConnectionRules(files(buildDirectory).filter(hasSuffix("awscredentials")).map(asProperties()).map(asAwsCredentials())));
    }

    private static Callable1<Artifact,URLConnection> s3ConnectionRules(final Sequence<AwsCredentials> awsCredentials) {
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
                return Rule.rule(awsCredentials, S3Connector.s3ConnectionUsing(awsCredentials));
            }
        };
    }

    private static Function1<java.util.Properties, AwsCredentials> asAwsCredentials() {
        return new Function1<java.util.Properties, AwsCredentials>() {
            @Override
            public AwsCredentials call(java.util.Properties properties) throws Exception {
                return awsCredentials(properties.getProperty("pattern"), properties(new File(properties.getProperty("file"))));
            }
        };
    }

    private static Function1<File,java.util.Properties> asProperties() {
        return new Function1<File, java.util.Properties>() {
            @Override
            public java.util.Properties call(File file) throws Exception {
                return properties(file);
            }
        };
    }

    private static java.util.Properties properties(File file) throws FileNotFoundException {
        return using(new FileInputStream(file), new Callable1<FileInputStream, java.util.Properties>() {
            @Override
            public java.util.Properties call(FileInputStream fileInputStream) throws Exception {
                return Properties.properties(fileInputStream);
            }
        });
    }

}
