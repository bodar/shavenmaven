package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.regex.Regex;

import java.util.regex.MatchResult;

import static com.googlecode.totallylazy.Sequences.sequence;

public enum MvnArtifacts implements Artifacts { instance;
    public static final String PROTOCOL = "mvn";
    private static Regex regex = Regex.regex("mvn:(//.+/)?([^:]+):([^:]+):([^:]+):([\\d\\w\\.\\-]+)");

    @Override
    public String scheme() { return PROTOCOL; }

    @Override
    public Sequence<MvnArtifact> parse(final String value) {
            if (!regex.matches(value)) {
                throw new IllegalArgumentException("Can only parse mvn: urls " + value);
            }
            final MatchResult match = regex.findMatches(value).head();

            return sequence(match.group(4).split("\\|")).map(new Function1<String, MvnArtifact>() {
                public MvnArtifact call(String type) throws Exception {
                    String repository = repository(match.group(1));
                    String group = match.group(2);
                    String id = match.group(3);
                    String version = match.group(5);
                    return new MvnArtifact(repository, group, id, version, type, value);
                }
            });
        }

    private static String repository(String host) {
        return host == null ? defaultRepository() : "http:" + host;
    }

    public static final String KEY = "shavenmaven.default-repository";

    public static String defaultRepository() {
        return System.getProperty(KEY, "http://repo1.maven.org/maven2/");
    }

    public static String defaultRepository(String value) {
        if (Strings.isEmpty(value)) return System.clearProperty(KEY);
        return System.setProperty(KEY, value);
    }

}
