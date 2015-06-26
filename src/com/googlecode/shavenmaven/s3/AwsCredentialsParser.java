package com.googlecode.shavenmaven.s3;

import com.googlecode.shavenmaven.config.SectionedProperties;
import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.functions.Callables;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.empty;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Unchecked.cast;
import static java.lang.String.format;

public class AwsCredentialsParser {
    private static final String AUTH_SECTION = "auth";
    private static final String PREFIX = "prefix";
    private static final String ACCESS_KEY = "access_key";
    private static final String SECRET_KEY = "secret_key";

    public static Sequence<AwsCredentials> awsCredentials(SectionedProperties props) {
        return props.section(AUTH_SECTION).
                map(properties -> Maps.<String, String>pairs(cast(properties))).
                getOrElse(empty()).
                groupBy(AwsCredentialsParser::prefix).
                map(AwsCredentialsParser::toAwsCredentials);
    }

    private static AwsCredentials toAwsCredentials(Group<String, Pair<String, String>> props) throws Exception {
            return AwsCredentials.awsCredentials(property(props, PREFIX), property(props, ACCESS_KEY), property(props, SECRET_KEY));
    }

    private static String property(Sequence<Pair<String, String>> props, String prefix) throws Exception {
        return props.filter(p -> p.first().contains(prefix)).
                headOption().
                map(Callables.<String>second()).
                getOrThrow(missingProperty(props, prefix));
    }

    private static String prefix(Pair<String, String> p) {
        return sequence(p.first().split("\\.")).headOption().getOrNull();
    }

    private static Exception missingProperty(Sequence<Pair<String, String>> props, String property) {
        return new RuntimeException(format("No property for '%s' in '%s'", property, props.toString("\n")));
    }
}
