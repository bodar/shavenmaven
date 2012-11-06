package com.googlecode.shavenmaven;

import java.util.regex.Pattern;

public class S3Artifact extends DelegatingArtifact {
    public static String PROTOCOL = "s3";
    private static Pattern pattern = Pattern.compile(PROTOCOL + "\\:\\/\\/([^\\/]+)/");

    protected S3Artifact(Artifact artifact) {
        super(artifact);
    }

    public static Iterable<MvnArtifact> parse(final String value) {
        return MvnArtifact.parse(pattern.matcher(value).replaceFirst("mvn://$1.s3.amazonaws.com/"));
    }
}
