package com.googlecode.shavenmaven;

import com.googlecode.totallylazy.Sequence;
import org.junit.Test;

import java.util.ArrayList;

import static com.googlecode.totallylazy.Sequences.sequence;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

public class PomGeneratorTest {
    @Test
    public void supportsStandardArtifactDetails() throws Exception {
        String pom = new PomGenerator().generate(sequence(MvnArtifacts.instance.parse("mvn:com.googlecode.shavenmaven:shavenmaven:jar:18")).head(), new ArrayList<Artifact>());
        assertThat(pom, containsString("<groupId>com.googlecode.shavenmaven</groupId>"));
        assertThat(pom, containsString("<artifactId>shavenmaven</artifactId>"));
        assertThat(pom, containsString("<version>18</version>"));
    }

    @Test
    public void supportsDependencies() throws Exception {
        Iterable<MvnArtifact> dependencies = sequence(MvnArtifacts.instance.parse("mvn:com.googlecode.totallylazy:totallylazy:jar|sources:207")).join(MvnArtifacts.instance.parse("mvn:com.googlecode.yadic:yadic:jar:116"));
        String pom = new PomGenerator().generate(sequence(MvnArtifacts.instance.parse("mvn:com.googlecode.shavenmaven:shavenmaven:jar:18")).head(), dependencies);
        assertThat(unformat(pom), is(unformat("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"" +
                "         xmlns=\"http://maven.apache.org/POM/4.0.0\"" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "    <modelVersion>4.0.0</modelVersion>" +
                "    <groupId>com.googlecode.shavenmaven</groupId>" +
                "    <artifactId>shavenmaven</artifactId>" +
                "    <version>18</version>" +
                "    <name>shavenmaven</name>" +
                "    <dependencies>" +
                "        <dependency>" +
                "            <groupId>com.googlecode.totallylazy</groupId>" +
                "            <artifactId>totallylazy</artifactId>" +
                "            <version>207</version>" +
                "        </dependency>" +
                "        <dependency>" +
                "            <groupId>com.googlecode.yadic</groupId>" +
                "            <artifactId>yadic</artifactId>" +
                "            <version>116</version>" +
                "        </dependency>" +
                "    </dependencies>" +
                "</project>")));
    }

    @Test
    public void supportsNoDependencies() throws Exception {
        Iterable<MvnArtifact> dependencies = sequence();
        String pom = new PomGenerator().generate(sequence(MvnArtifacts.instance.parse("mvn:com.googlecode.shavenmaven:shavenmaven:jar:18")).head(), dependencies);
        assertThat(unformat(pom), is(unformat("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"" +
                "         xmlns=\"http://maven.apache.org/POM/4.0.0\"" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "    <modelVersion>4.0.0</modelVersion>" +
                "    <groupId>com.googlecode.shavenmaven</groupId>" +
                "    <artifactId>shavenmaven</artifactId>" +
                "    <version>18</version>" +
                "    <name>shavenmaven</name>" +
                "    <dependencies>" +
                "    </dependencies>" +
                "</project>")));
    }

    @Test
    public void ignoresUrlDependencies() throws Exception {
        Sequence<UrlArtifact> dependencies = sequence(UrlArtifacts.instance.parse("http://server/path"));
        String pom = new PomGenerator().generate(sequence(MvnArtifacts.instance.parse("mvn:com.googlecode.shavenmaven:shavenmaven:jar:18")).head(), dependencies);
        assertThat(unformat(pom), is(unformat("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\"" +
                "         xmlns=\"http://maven.apache.org/POM/4.0.0\"" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "    <modelVersion>4.0.0</modelVersion>" +
                "    <groupId>com.googlecode.shavenmaven</groupId>" +
                "    <artifactId>shavenmaven</artifactId>" +
                "    <version>18</version>" +
                "    <name>shavenmaven</name>" +
                "    <dependencies>" +
                "    </dependencies>" +
                "</project>")));
    }

    private String unformat(String value) {
        return value.replaceAll("\\s+", "");
    }
}
