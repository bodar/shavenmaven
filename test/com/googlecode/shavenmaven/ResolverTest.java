package com.googlecode.shavenmaven;

import com.googlecode.shavenmaven.mvn.Handler;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static com.googlecode.shavenmaven.Resolver.url;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ResolverTest {
    @Test
    public void supportsHttp() throws Exception{
        File directory = new TemporaryDirectory();
        Resolver resolver = new Resolver(directory);

        assertThat(directory.listFiles().length, is(0));
        resolver.resolve(url("http://yatspec.googlecode.com/files/yatspec-87.jar"));
        assertThat(directory.listFiles().length, is(1));
        System.out.println(directory);
    }

    @Test
    public void supportsMvn() throws Exception{
        File directory = new TemporaryDirectory();
        Resolver resolver = new Resolver(directory);

        assertThat(directory.listFiles().length, is(0));
        resolver.resolve(url("mvn://repo.bodar.com/com.googlecode.yadic/yadic/116/jar"));
        assertThat(directory.listFiles().length, is(1));
        System.out.println(directory);
    }
}
