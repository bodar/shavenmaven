package com.googlecode.shavenmaven;

import com.googlecode.shavenmaven.config.SectionedProperties;
import com.googlecode.shavenmaven.s3.AwsCredentials;
import com.googlecode.shavenmaven.s3.AwsCredentialsParser;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.SystemClock;

import java.io.File;

import static com.googlecode.shavenmaven.CompositeArtifacts.compositeArtifacts;
import static com.googlecode.shavenmaven.S3Artifacts.s3Artifacts;
import static com.googlecode.shavenmaven.s3.AwsCredentials.environmentCredentials;
import static com.googlecode.shavenmaven.s3.AwsCredentialsParser.awsCredentials;
import static com.googlecode.totallylazy.Option.option;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Sequences.flatten;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.*;
import static java.lang.System.getProperty;

public class SupportedArtifacts implements Artifacts {
    private final Artifacts artifacts;

    private SupportedArtifacts(Artifacts artifacts) {
        this.artifacts = artifacts;
    }

    public static SupportedArtifacts supportedArtifacts(Artifacts artifacts) {
        return new SupportedArtifacts(artifacts);
    }

    public static SupportedArtifacts supportedArtifacts() {
        return supportedArtifacts(new SystemClock());
    }

    public static SupportedArtifacts supportedArtifacts(Clock clock) {
        Option<AwsCredentials> environment = environmentCredentials();
        return supportedArtifacts(clock, environment.isDefined() ? sequence(environment) : awsCredentials(SectionedProperties.sectionedProperties()));
    }

    private static SupportedArtifacts supportedArtifacts(Clock clock, Sequence<AwsCredentials> credentials) {
        return supportedArtifacts(compositeArtifacts(sequence(MvnArtifacts.instance, s3Artifacts(clock, credentials), UrlArtifacts.instance)));
    }

    private static Mapper<File, String> read() {
        return new Mapper<File, String>() {
            @Override
            public String call(File file) throws Exception {
                return string(file);
            }
        };
    }

    public Artifact artifact(String value) {
        return sequence(parse(value)).head();
    }

    public Sequence<Artifact> artifacts(Option<File> file) {
        return flatten(file.map(toArtifacts()));
    }

    private Callable1<File, Sequence<Artifact>> toArtifacts() {
        return new Callable1<File, Sequence<Artifact>>() {
            public Sequence<Artifact> call(File file) throws Exception {
                return toArtifacts(lines(file));
            }
        };
    }

    public Sequence<Artifact> toArtifacts(Sequence<String> lines) {
        return lines.filter(not(empty().or(startsWith("#")))).flatMap(Artifacts.functions.asArtifact(this)).memorise();
    }

    @Override
    public String scheme() {
        return artifacts.scheme();
    }

    @Override
    public Iterable<? extends Artifact> parse(String value) {
        return artifacts.parse(value);
    }
}
