package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;

import java.util.regex.Pattern;

import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.String.format;
import static java.util.regex.Pattern.compile;

public class S3Artifact extends DelegatingArtifact<MvnArtifact> {
    public static String PROTOCOL = "s3";
    private static Pattern toMvn = compile(PROTOCOL + "\\:\\/\\/([^\\/]+)/");
    private static Pattern fromMvn = compile(MvnArtifact.PROTOCOL + "\\:\\/\\/([^\\/]+)\\.s3\\.amazonaws\\.com/");

    protected S3Artifact(MvnArtifact artifact) {
        super(artifact);
    }

    @Override
    public String toString() {
        return format("%s (%s)", artifact.uri(), value());
    }

    public String value() {
        return fromMvn.matcher(artifact.value()).replaceFirst("s3://$1/");
    }

    public static Iterable<S3Artifact> parse(final String value) {
        return sequence(MvnArtifact.parse(toMvn.matcher(value).replaceFirst("mvn://$1.s3.amazonaws.com/"))).map(s3Artifact);
    }

    public static Function1<MvnArtifact, S3Artifact> s3Artifact = new Function1<MvnArtifact, S3Artifact>() {
        @Override
        public S3Artifact call(MvnArtifact mvnArtifact) throws Exception {
            return new S3Artifact(mvnArtifact);
        }
    };
}
