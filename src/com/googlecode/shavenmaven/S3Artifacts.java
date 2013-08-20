package com.googlecode.shavenmaven;

import com.googlecode.shavenmaven.s3.AwsCredentials;
import com.googlecode.shavenmaven.s3.S3Signer;
import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.totallylazy.time.Clock;
import com.googlecode.totallylazy.time.SystemClock;
import com.googlecode.utterlyidle.Request;

import java.util.regex.Pattern;

import static com.googlecode.totallylazy.Sequences.sequence;
import static java.util.regex.Pattern.compile;

public class S3Artifacts implements Artifacts {
    public static final String PROTOCOL = "s3";
    private static Pattern toMvn = compile(PROTOCOL + "\\:\\/\\/([^\\/]+)/");
    private final Sequence<AwsCredentials> credentials;
    private final Clock clock;

    private S3Artifacts(Clock clock, Sequence<AwsCredentials> credentials) {
        this.credentials = credentials;
        this.clock = clock;
    }

    public static S3Artifacts s3Artifacts(Clock clock, Sequence<AwsCredentials> credentials) {
        return new S3Artifacts(clock, credentials);
    }

    public static S3Artifacts s3Artifacts(Clock clock, AwsCredentials... credentials) {
        return s3Artifacts(clock, sequence(credentials));
    }

    public static S3Artifacts s3Artifacts() {
        return s3Artifacts(new SystemClock());
    }

    @Override
    public String scheme() {
        return PROTOCOL;
    }

    @Override
    public Sequence<S3Artifact> parse(String value) {
        return sequence(MvnArtifacts.instance.parse(toMvn.matcher(value).replaceFirst("mvn://$1.s3.amazonaws.com/"))).map(s3Artifact(Uri.uri(value)));
    }

    public Function1<MvnArtifact, S3Artifact> s3Artifact(final Uri originalUri) {
        return new Function1<MvnArtifact, S3Artifact>() {
            @Override
            public S3Artifact call(MvnArtifact mvnArtifact) throws Exception {
                return new S3Artifact(originalUri, mvnArtifact, credentials(originalUri).fold(mvnArtifact.request(), sign()));
            }
        };
    }

    private Option<AwsCredentials> credentials(final Uri artifact) {
        return credentials.find(new LogicalPredicate<AwsCredentials>() {
            @Override
            public boolean matches(AwsCredentials other) {
                return other.matches(artifact);
            }
        });
    }

    private Function2<Request, AwsCredentials, Request> sign() {
        return new Function2<Request, AwsCredentials, Request>() {
            @Override
            public Request call(Request request, AwsCredentials awsCredentials) throws Exception {
                return new S3Signer(awsCredentials, clock).sign(request);
            }
        };
    }

}
