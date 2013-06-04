package com.googlecode.shavenmaven.s3;

import com.googlecode.shavenmaven.config.SectionedProperties;
import com.googlecode.totallylazy.Sequence;
import junit.framework.Assert;
import org.junit.Test;

import static com.googlecode.shavenmaven.config.SectionedProperties.sectionedProperties;
import static com.googlecode.shavenmaven.s3.AwsCredentialsParser.awsCredentials;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class AwsCredentialsParserTest {

    @Test
    public void createsCredentialsFromPropertyFormattedProperties() throws Exception {
        SectionedProperties properties = sectionedProperties(
                "[auth]\n" +
                        "one.prefix = s3://bucket\n\n" +
                        "one.access_key=meerkatteam\n" +
                        "one.secret_key=letmein\n" +
                        "\n" +
                        "two.prefix = s3://blacket\n\n" +
                        "two.access_key=bleet\n" +
                        "two.secret_key=blooby\n"
        );
        final Sequence<AwsCredentials> awsCredentials = awsCredentials(properties);
        assertThat(awsCredentials.size(), is(2));

        assertThat(awsCredentials.first().pattern(), is("s3://blacket"));
        assertThat(awsCredentials.first().accessKeyId(), is("bleet"));
        assertThat(awsCredentials.first().secretKey(), is("blooby"));

        assertThat(awsCredentials.second().pattern(), is("s3://bucket"));
        assertThat(awsCredentials.second().accessKeyId(), is("meerkatteam"));
        assertThat(awsCredentials.second().secretKey(), is("letmein"));
    }

    @Test
    public void throwsExceptionIfCredentialsAreMisformed() {
        SectionedProperties properties = sectionedProperties(
                "[auth]\n" +
                        "one.prefix = s3://bucket\n" +
                        "one.secret_key=letmein\n"

        );
        try {
            awsCredentials(properties).realise();
            Assert.fail("did not get expected exception");
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("No property for 'access_key' in"));
        }

    }
}
