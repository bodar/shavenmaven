package com.googlecode.shavenmaven;

import org.junit.Test;

import java.io.File;

import static com.googlecode.shavenmaven.Resolver.write;

public class DependenciesTest {
    @Test
    public void supportsLoadingFromFile() throws Exception{
        File temporaryFile = new TemporaryFile();
        write(("http://yatspec.googlecode.com/files/yatspec-87.jar\n" +
             "mvn://repo.bodar.com/com.googlecode.yadic/yadic/116/jar").getBytes(), temporaryFile);
        Dependencies dependencies = Dependencies.load(temporaryFile);
        TemporaryDirectory temporaryDirectory = new TemporaryDirectory();
        dependencies.update(temporaryDirectory);
        System.out.println("temporaryDirectory = " + temporaryDirectory);
    }
}
