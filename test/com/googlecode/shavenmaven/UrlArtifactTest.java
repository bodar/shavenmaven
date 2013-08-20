package com.googlecode.shavenmaven;

import org.junit.Test;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UrlArtifactTest {
    @Test
    public void supportVanillaHttpUrl() throws Exception {
        UrlArtifact artifact = UrlArtifact.parse("http://yatspec.googlecode.com/files/yatspec-87.jar");
        assertThat(artifact.uri().toString(), is(new URL("http://yatspec.googlecode.com/files/yatspec-87.jar").toString()));
        assertThat(artifact.filename(), is("yatspec-87.jar"));
    }
}
