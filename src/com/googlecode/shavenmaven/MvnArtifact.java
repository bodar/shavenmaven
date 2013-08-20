package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.regex.Regex;

import java.util.regex.MatchResult;

import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.String.format;

public class MvnArtifact implements Artifact {
    public static final String KEY = "shavenmaven.default-repository";

    public static String defaultRepository() {
        return System.getProperty(KEY, "http://repo1.maven.org/maven2/");
    }

    public static String defaultRepository(String value) {
        if (Strings.isEmpty(value)) {
            return System.clearProperty(KEY);
        }
        return System.setProperty(KEY, value);
    }

    public static final String PROTOCOL = "mvn";
    private static Regex regex = Regex.regex("mvn:(//.+/)?([^:]+):([^:]+):([^:]+):([\\d\\w\\.\\-]+)");
    private final String repository;
    private final String group;
    private final String id;
    private final String version;
    private final String type;
    private final String value;

    private MvnArtifact(String repository, String group, String id, String version, String type, String value) {
        this.repository = repository;
        this.group = group;
        this.id = id;
        this.version = version;
        this.type = type;
        this.value = value;
    }

    public static Iterable<MvnArtifact> parse(final String value) {
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

    private static String replaceDots(String value) {
        return value.replace('.', '/');
    }

    public String group() {
        return group;
    }

    public String id() {
        return id;
    }

    public String version() {
        return version;
    }

    public String type() {
        return type;
    }

    public String value() {
        return value;
    }

    private String filesuffix() {
        return "jar".equals(type) ? ".jar" : format("-%s.jar", type);
    }

    public Uri uri() {
        return Uri.uri(repository + path());
    }

    private String path() {
        return String.format("%s/%s/%s/%s",
                replaceDots(group()),
                id(),
                version(),
                filename());
    }

    public String filename() {
        return format("%s-%s%s", id(), version(), filesuffix());
    }

    @Override
    public String toString() {
        return format("%s (%s)", uri(), value);
    }
}
