package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Sequences;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.sequence;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

public class PomGeneratorTest {
    @Test
    public void supportsStandardArtifactDetails() throws Exception {
        String pom = new PomGenerator().generate(new MvnArtifact("mvn:com.googlecode.shavenmaven:shavenmaven:jar:18"), new ArrayList<Artifact>());
        assertThat(pom, containsString("<groupId>com.googlecode.shavenmaven</groupId>"));
        assertThat(pom, containsString("<artifactId>shavenmaven</artifactId>"));
        assertThat(pom, containsString("<version>18</version>"));
    }

    @Test
    public void supportsDependencies() throws Exception {
        Iterable<MvnArtifact> dependencies = sequence(new MvnArtifact("mvn:com.googlecode.totallylazy:totallylazy:jar:207"));
        String pom = new PomGenerator().generate(new MvnArtifact("mvn:com.googlecode.shavenmaven:shavenmaven:jar:18"), dependencies);
        assertThat(pom, containsString("<groupId>com.googlecode.totallylazy</groupId>"));
        assertThat(pom, containsString("<artifactId>totallylazy</artifactId>"));
        assertThat(pom, containsString("<version>207</version>"));
    }
}
