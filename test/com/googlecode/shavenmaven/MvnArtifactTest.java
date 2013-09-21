package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Sequence;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MvnArtifactTest {
    @Test
    public void supportsPack200() throws Exception {
        Sequence<MvnArtifact> artifacts = MvnArtifacts.instance.parse("mvn://repo.bodar.com/someFolder/com.googlecode.totallylazy:totallylazy:pack|tests-pack:1125");
        MvnArtifact mvnArtifact = artifacts.first();
        assertThat(mvnArtifact.group(), is("com.googlecode.totallylazy"));
        assertThat(mvnArtifact.id(), is("totallylazy"));
        assertThat(mvnArtifact.type(), is("pack"));
        assertThat(mvnArtifact.version(), is("1125"));
        assertThat(mvnArtifact.value().toString(), is("mvn://repo.bodar.com/someFolder/com.googlecode.totallylazy:totallylazy:pack|tests-pack:1125"));
        assertThat(mvnArtifact.uri().toString(), is(new URL("http://repo.bodar.com/someFolder/com/googlecode/totallylazy/totallylazy/1125/totallylazy-1125.pack.gz").toString()));
        assertThat(mvnArtifact.filename(), is("totallylazy-1125.jar"));

        MvnArtifact tests = artifacts.second();
        assertThat(tests.group(), is("com.googlecode.totallylazy"));
        assertThat(tests.id(), is("totallylazy"));
        assertThat(tests.type(), is("tests-pack"));
        assertThat(tests.version(), is("1125"));
        assertThat(tests.value().toString(), is("mvn://repo.bodar.com/someFolder/com.googlecode.totallylazy:totallylazy:pack|tests-pack:1125"));
        assertThat(tests.uri().toString(), is(new URL("http://repo.bodar.com/someFolder/com/googlecode/totallylazy/totallylazy/1125/totallylazy-1125-tests.pack.gz").toString()));
        assertThat(tests.filename(), is("totallylazy-1125-tests.jar"));
    }

    @Test
    public void canChangeTheDefaultRepository() throws Exception {
        String original = MvnArtifacts.defaultRepository("http://uk.maven.org/maven2/");
        MvnArtifact mvnArtifact = MvnArtifacts.instance.parse("mvn:org.objenesis:objenesis:jar|sources:1.2").head();
        assertThat(mvnArtifact.uri().toString(), is(new URL("http://uk.maven.org/maven2/org/objenesis/objenesis/1.2/objenesis-1.2.jar").toString()));
        MvnArtifacts.defaultRepository(original);
    }

    @Test
    public void supportsUriWithExplicitRepositoryAndRootFolder() throws Exception {
        MvnArtifact mvnArtifact = MvnArtifacts.instance.parse("mvn://repo.bodar.com/someFolder/com.googlecode.yadic:yadic:jar:116").head();
        assertThat(mvnArtifact.group(), is("com.googlecode.yadic"));
        assertThat(mvnArtifact.id(), is("yadic"));
        assertThat(mvnArtifact.type(), is("jar"));
        assertThat(mvnArtifact.version(), is("116"));
        assertThat(mvnArtifact.value().toString(), is("mvn://repo.bodar.com/someFolder/com.googlecode.yadic:yadic:jar:116"));
        assertThat(mvnArtifact.uri().toString(), is(new URL("http://repo.bodar.com/someFolder/com/googlecode/yadic/yadic/116/yadic-116.jar").toString()));
        assertThat(mvnArtifact.filename(), is("yadic-116.jar"));
    }

    @Test
    public void supportsMultipleTypesInASingleUrl() throws Exception {
        Sequence<MvnArtifact> mvnArtifact = MvnArtifacts.instance.parse("mvn:org.objenesis:objenesis:jar|sources:1.2");
        MvnArtifact jar = mvnArtifact.first();
        assertThat(jar.group(), is("org.objenesis"));
        assertThat(jar.id(), is("objenesis"));
        assertThat(jar.type(), is("jar"));
        assertThat(jar.version(), is("1.2"));
        assertThat(jar.value().toString(), is("mvn:org.objenesis:objenesis:jar|sources:1.2"));
        assertThat(jar.uri().toString(), is(new URL("http://repo1.maven.org/maven2/org/objenesis/objenesis/1.2/objenesis-1.2.jar").toString()));
        assertThat(jar.filename(), is("objenesis-1.2.jar"));

        MvnArtifact second = mvnArtifact.second();
        assertThat(second.group(), is("org.objenesis"));
        assertThat(second.id(), is("objenesis"));
        assertThat(second.type(), is("sources"));
        assertThat(second.version(), is("1.2"));
        assertThat(second.value().toString(), is("mvn:org.objenesis:objenesis:jar|sources:1.2"));
        assertThat(second.uri().toString(), is(new URL("http://repo1.maven.org/maven2/org/objenesis/objenesis/1.2/objenesis-1.2-sources.jar").toString()));
        assertThat(second.filename(), is("objenesis-1.2-sources.jar"));
    }

    @Test
    public void supportsAlphaVersionsWithHyphens() throws Exception {
        MvnArtifact mvnArtifact = MvnArtifacts.instance.parse("mvn:org.sitemesh:sitemesh:jar:3.0-alpha-2").head();
        assertThat(mvnArtifact.group(), is("org.sitemesh"));
        assertThat(mvnArtifact.id(), is("sitemesh"));
        assertThat(mvnArtifact.type(), is("jar"));
        assertThat(mvnArtifact.version(), is("3.0-alpha-2"));
        assertThat(mvnArtifact.uri().toString(), is(new URL("http://repo1.maven.org/maven2/org/sitemesh/sitemesh/3.0-alpha-2/sitemesh-3.0-alpha-2.jar").toString()));
        assertThat(mvnArtifact.filename(), is("sitemesh-3.0-alpha-2.jar"));
    }

    @Test
    public void supportsUriWithNoExplicitRepository() throws Exception {
        MvnArtifact mvnArtifact = MvnArtifacts.instance.parse("mvn:org.objenesis:objenesis:jar:1.2").head();
        assertThat(mvnArtifact.group(), is("org.objenesis"));
        assertThat(mvnArtifact.id(), is("objenesis"));
        assertThat(mvnArtifact.type(), is("jar"));
        assertThat(mvnArtifact.version(), is("1.2"));
        assertThat(mvnArtifact.uri().toString(), is(new URL("http://repo1.maven.org/maven2/org/objenesis/objenesis/1.2/objenesis-1.2.jar").toString()));
        assertThat(mvnArtifact.filename(), is("objenesis-1.2.jar"));
    }

    @Test
    public void supportsUriWithExplicitRepository() throws Exception {
        MvnArtifact mvnArtifact = MvnArtifacts.instance.parse("mvn://repo.bodar.com/com.googlecode.yadic:yadic:jar:116").head();
        assertThat(mvnArtifact.group(), is("com.googlecode.yadic"));
        assertThat(mvnArtifact.id(), is("yadic"));
        assertThat(mvnArtifact.type(), is("jar"));
        assertThat(mvnArtifact.version(), is("116"));
        assertThat(mvnArtifact.uri().toString(), is(new URL("http://repo.bodar.com/com/googlecode/yadic/yadic/116/yadic-116.jar").toString()));
        assertThat(mvnArtifact.filename(), is("yadic-116.jar"));
    }
    
    @Test
    public void understandsClassifiers() throws MalformedURLException {
        MvnArtifact mvnArtifact = MvnArtifacts.instance.parse("mvn://repo.bodar.com/com.googlecode.yadic:yadic:classifier:116").head();
        assertThat(mvnArtifact.group(), is("com.googlecode.yadic"));
        assertThat(mvnArtifact.id(), is("yadic"));
        assertThat(mvnArtifact.type(), is("classifier"));
        assertThat(mvnArtifact.version(), is("116"));
        assertThat(mvnArtifact.uri().toString(), is(new URL("http://repo.bodar.com/com/googlecode/yadic/yadic/116/yadic-116-classifier.jar").toString()));
        assertThat(mvnArtifact.filename(), is("yadic-116-classifier.jar"));
    }
}
