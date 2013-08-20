package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Sequence;
import org.junit.Test;

import java.io.StringReader;

import static com.googlecode.totallylazy.Strings.lines;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static org.junit.Assert.assertThat;

public class ArtifactsTest {
    @Test
    public void ignoreComments() throws Exception {
        Sequence<Artifact> artifacts = SupportedArtifacts.supportedArtifacts().toArtifacts(lines(new StringReader("#Hello Raymond\nhttp://server/boo")));
        assertThat(artifacts.size(), is(1));
    }
}
