## Example ##

Create a text file with some dependencies in it. Lets call it `build.dependencies`

```
# This is a comment
mvn:org.hamcrest:hamcrest-core:jar:1.2.1
mvn://repo.bodar.com/com.googlecode.totallylazy:totallylazy:pack|sources:1125
s3://repo.bodar.com/com.googlecode.yadic:yadic:jar:151
http://jarjar.googlecode.com/files/jarjar-1.1.jar
jar:https://storage.googleapis.com/simba-bq-release/jdbc/SimbaJDBCDriverforGoogleBigQuery42_1.1.0.1000.zip!/GoogleBigQueryJDBC42.jar
file:///home/dan/Project/foo.jar
```

ShavenMaven uses an extended form of the [BuildR](http://buildr.apache.org/) mvn url. (This means when you use sites like http://mvnrepository.com/ you can click on the BuildR tab and just prefix it with "mvn:")

  * The first line after the comment will download `hamcrest-core` jar from the central repository.
  * The second line will download `totallylazy` [pack200](http://docs.oracle.com/javase/7/docs/api/java/util/jar/Pack200.Packer.html) file (and unpack it to a jar) and sources from a custom repository `repo.bodar.com`.
  * The third line will download 'yadic' jar from 'repo.bodar.com.s3.amazonaws.com'
  * The fourth line will do a plain HTTP GET request to non mavenised jar.
  * The fifth line will download a jar/zip and extract a file inside
  * The last line will look for a local file called foo.jar

You can also specify an alternative default repo for mvn urls with
`-Dshavenmaven.default-repository=http://uk.maven.org/maven2/`


### Command line usage ###

To actually download the jars, run the following:
```
java -jar shavenmaven.jar build.dependencies lib/build
```

Where `lib/build` is a directory where you want to download the jars to.

ShaveMaven also recognizes some command line options that slighty alter its behavior, for example:
```
java -jar shavenmaven.jar --quiet build.dependencies lib/build
```

To see a list of available command line options just run without any arguments:
```
java -jar shavenmaven.jar
```

You can also update multiple dependencies files and directories at the same time by providing 2 directories:

```
java -jar shavenmaven.jar build lib
```

This will recursively find all ".dependencies" files in "build" and create a directory with a matching name in "lib" so:

  * build/
    * build.dependencies
    * runtime.dependencies
    * optional/
      * unix-specific.dependencies
      * windows-specific.dependencies

Would create the following structure:

  * lib/
    * build/
    * runtime/
    * optional/
      * unix-specific/
      * windows-specific/


### ANT usage ###

To make life easy in ANT, ShavenMaven provides a [macro file](src/shavenmaven.xml).
Obviously ANT is xml based but it's all just syntactic sugar over the command line interface.

Importing this macro file will allow you to install ShavenMaven with a single line:

```
<install version="37"/>
```

You can then update a directory just like in the command line version

```
<update dependencies="build.dependencies" directory="lib/build"/>
```

or to do multiple files and directories in one go

```
<update dependencies="build" directory="lib"/>
```


You can also generate a maven pom from the dependencies file with something like:

```
<generate.pom artifact.uri="mvn:${groupid}:${artifactid}:jar:${version}"
 dependencies="lib/runtime.dependencies" directory="${artifacts}"/>
```

If you want to see all of this put together have a look at ShavenMavens [build file](build.xml)

