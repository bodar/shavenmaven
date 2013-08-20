package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;

import java.util.regex.Pattern;

import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.String.format;
import static java.util.regex.Pattern.compile;

public class S3Artifact extends DelegatingArtifact<MvnArtifact> {
    private static Pattern fromMvn = compile(MvnArtifacts.PROTOCOL + "\\:\\/\\/([^\\/]+)\\.s3\\.amazonaws\\.com/");

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
}
