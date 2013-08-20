package com.googlecode.shavenmaven.config;

import com.googlecode.totallylazy.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

import static com.googlecode.totallylazy.Maps.get;
import static com.googlecode.totallylazy.Maps.map;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Properties.properties;
import static com.googlecode.totallylazy.Sequences.empty;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.lines;
import static com.googlecode.totallylazy.Strings.startsWith;

public class SectionedProperties {

    private final String content;

    public static SectionedProperties sectionedProperties(String content) {
        return new SectionedProperties(content);
    }

    private SectionedProperties(String content) {
        this.content = content;
    }

    public Option<Properties> section(String section) {
        return get(map(lines(new StringReader(content)).recursive(splitSections(startsWith("["))).map(toProperties())), section);
    }

    private Callable1<Sequence<String>, Pair<String,Properties>> toProperties() {
        return new Callable1<Sequence<String>, Pair<String, Properties>>() {
            @Override
            public Pair<String, Properties> call(Sequence<String> s) throws Exception {
                return pair(s.first().replace("[", "").replace("]", ""), properties(inputStream(s.tail())));
            }
        };
    }

    private InputStream inputStream(Sequence<String> strings) {
        return new ByteArrayInputStream(strings.toString("\n").getBytes());
    }

    private Callable1<Sequence<String>, Pair<Sequence<String>, Sequence<String>>> splitSections(final Predicate<String> predicate) {
        return new Callable1<Sequence<String>, Pair<Sequence<String>, Sequence<String>>>() {
            @Override
            public Pair<Sequence<String>, Sequence<String>> call(Sequence<String> strings) throws Exception {
                final Pair<Sequence<String>, Sequence<String>> split = strings.breakOn(predicate);
                final Sequence<String> section = split.second();
                if(section.isEmpty()) {
                    return pair(empty(String.class), empty(String.class));
                }
                final Pair<Sequence<String>, Sequence<String>> sectionAndRest = section.drop(1).breakOn(predicate);
                final String first = section.first();
                final Sequence<String> first1 = sectionAndRest.first();
                return pair(sequence(first).join(first1), sectionAndRest.second());
            }
        };
    }
}
