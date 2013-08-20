package com.googlecode.shavenmaven.s3;

import com.googlecode.shavenmaven.config.SectionedProperties;
import com.googlecode.totallylazy.*;

import java.util.Map;
import java.util.Properties;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.lang.String.format;

public class AwsCredentialsParser {
    private static final String AUTH_SECTION = "auth";
    private static final String PREFIX = "prefix";
    private static final String ACCESS_KEY = "access_key";
    private static final String SECRET_KEY = "secret_key";

    public static Sequence<AwsCredentials> awsCredentials(SectionedProperties props) {
        return props.section(AUTH_SECTION).map(toPairs()).getOrElse(Sequences.<Pair<String, String>>empty()).groupBy(prefix()).map(toAwsCredentials());
    }

    private static Callable1<Group<String, Pair<String, String>>, AwsCredentials> toAwsCredentials() {
        return new Callable1<Group<String, Pair<String, String>>, AwsCredentials>() {
            @Override
            public AwsCredentials call(Group<String, Pair<String, String>> g) throws Exception {
                final Sequence<Pair<String, String>> props = sequence(g);
                return AwsCredentials.awsCredentials(property(props, PREFIX), property(props, ACCESS_KEY), property(props, SECRET_KEY));
            }
        };
    }

    private static String property(Sequence<Pair<String, String>> props, String prefix) throws Exception {
        return props.filter(firstContains(prefix)).headOption().map(Callables.<String>second()).getOrThrow(missingProperty(props, prefix));
    }

    private static Predicate<Pair<String, String>> firstContains(final String prefix) {
        return new Predicate<Pair<String, String>>() {
            @Override
            public boolean matches(Pair<String, String> p) {
                return p.first().contains(prefix);
            }
        };
    }

    private static Callable1<Pair<String, String>, String> prefix() {
        return new Callable1<Pair<String, String>, String>() {
            @Override
            public String call(Pair<String, String> p) throws Exception {
                return sequence(p.first().split("\\.")).headOption().getOrNull();
            }
        };
    }

    private static Callable1<java.util.Properties, Sequence<Pair<String, String>>> toPairs() {
        return new Callable1<java.util.Properties, Sequence<Pair<String, String>>>() {
            @Override
            public Sequence<Pair<String, String>> call(Properties properties) throws Exception {
                return sequence(properties.entrySet()).map(toPair());
            }
        };
    }

    private static Callable1<Map.Entry<Object, Object>, Pair<String, String>> toPair() {
        return new Callable1<Map.Entry<Object, Object>, Pair<String, String>>() {
            @Override
            public Pair<String, String> call(Map.Entry<Object, Object> e) throws Exception {
                return pair(e.getKey().toString(), e.getValue().toString());
            }
        };
    }

    private static Exception missingProperty(Sequence<Pair<String, String>> props, String property) {
        return new RuntimeException(format("No property for '%s' in '%s'", property, props.toString("\n")));
    }
}
