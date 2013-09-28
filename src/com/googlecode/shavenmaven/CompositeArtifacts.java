package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.collections.PersistentMap;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.totallylazy.collections.PersistentSortedMap.constructors.sortedMap;

public class CompositeArtifacts implements Artifacts {
    private final PersistentMap<String, Artifacts> map;

    private CompositeArtifacts(Iterable<? extends Artifacts> artifacts) {
        this.map = sortedMap(sequence(artifacts).map(byScheme()));
    }

    public static CompositeArtifacts compositeArtifacts(Iterable<? extends Artifacts> sequence) {
        return new CompositeArtifacts(sequence);
    }

    @Override
    public String scheme() {
        return "*";
    }

    @Override
    public Iterable<? extends Artifact> parse(String value) {
        return map.lookup(uri(value).scheme()).
                getOrElse(map.lookup("*").get()).
                parse(value);
    }

    private Mapper<Artifacts, Pair<String, Artifacts>> byScheme() {
        return new Mapper<Artifacts, Pair<String, Artifacts>>() {
            @Override
            public Pair<String, Artifacts> call(Artifacts artifacts) throws Exception {
                return Pair.pair(artifacts.scheme(), artifacts);
            }
        };
    }

}
