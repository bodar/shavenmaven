package com.googlecode.shavenmaven.config;

import com.googlecode.totallylazy.Option;
import org.junit.Test;

import java.util.Properties;

import static com.googlecode.shavenmaven.config.SectionedProperties.sectionedProperties;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SectionedPropertiesTest {

    @Test
    public void returnsNoneIfSectionDoesNotExist() throws Exception {
        assertThat(sectionedProperties("").section("impossible").isEmpty(), is(true));
    }

    @Test
    public void returnsPropertiesForSection() throws Exception {
        final Option<Properties> auth = sectionedProperties(
                "[default]\n" +
                "some.prop = some.value\n" +
                "[auth]\n" +
                "privates3.prefix = s3://solo-commons\n\n" +
                "privates3.access_key=meerkatteam\n")
                .section("auth");
        assertThat(auth.get().getProperty("privates3.prefix"), is("s3://solo-commons"));
        assertThat(auth.get().getProperty("privates3.access_key"), is("meerkatteam"));
        assertThat(auth.get().size(), is(2));
    }
}
