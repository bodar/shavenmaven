package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Sequence;
import org.junit.Test;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.lines;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static org.junit.Assert.assertThat;

public class ArtifactsTest {
    @Test
    public void ignoreComments() throws Exception {
        Sequence<Artifact> artifacts = SupportedArtifacts.supportedArtifacts().toArtifacts(sequence("#Hello Raymond", "http://server/boo"));
        assertThat(artifacts.size(), is(1));
    }
}
