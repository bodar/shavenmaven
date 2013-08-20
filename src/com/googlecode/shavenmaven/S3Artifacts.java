package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Sequence;

import java.util.regex.Pattern;

import static com.googlecode.totallylazy.Sequences.sequence;
import static java.util.regex.Pattern.compile;

public enum S3Artifacts implements Artifacts { instance;
    public static String PROTOCOL = "s3";
    private static Pattern toMvn = compile(PROTOCOL + "\\:\\/\\/([^\\/]+)/");


    @Override
    public String scheme() {
        return PROTOCOL;
    }

    @Override
    public Sequence<S3Artifact> parse(String value) {
        return sequence(MvnArtifacts.instance.parse(toMvn.matcher(value).replaceFirst("mvn://$1.s3.amazonaws.com/"))).map(s3Artifact);
    }

    public static Function1<MvnArtifact, S3Artifact> s3Artifact = new Function1<MvnArtifact, S3Artifact>() {
        @Override
        public S3Artifact call(MvnArtifact mvnArtifact) throws Exception {
            return new S3Artifact(mvnArtifact);
        }
    };
}
