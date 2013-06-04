package com.googlecode.shavenmaven;

import com.googlecode.shavenmaven.config.SectionedProperties;
import com.googlecode.shavenmaven.s3.AwsCredentials;
import com.googlecode.shavenmaven.s3.S3Connector;
import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLConnection;

import static com.googlecode.shavenmaven.config.SectionedProperties.sectionedProperties;
import static com.googlecode.shavenmaven.s3.AwsCredentials.awsCredentials;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Files.fileOption;
import static com.googlecode.totallylazy.Files.files;
import static com.googlecode.totallylazy.Predicates.always;
import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Rule.rule;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.contains;
import static com.googlecode.totallylazy.Strings.lines;
import static java.lang.System.getProperty;

public class ConnectionRules {

    public static Rules<Artifact, URLConnection> connectByUrlRules() {
        final Rule<Artifact, URLConnection> rule = alwaysConnectByUrl();
        return Rules.<Artifact, URLConnection>rules(sequence(rule));
    }

    public static Rule<Artifact, URLConnection> alwaysConnectByUrl() {
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

}
