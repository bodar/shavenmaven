package com.googlecode.shavenmaven;

import java.util.regex.Pattern;

public class S3Artifact extends DelegatingArtifact {
    protected S3Artifact(Artifact artifact) {
        super(artifact);
    }

    private static Pattern pattern = Pattern.compile("s3\\:\\/\\/([^\\/]+)/");

    public static Iterable<MvnArtifact> parse(final String value) {
        return MvnArtifact.parse(pattern.matcher(value).replaceFirst("mvn://$1.s3.amazonaws.com/"));
    }
}
