Build Java Project
=======================

You need to download and install the maven for build java project.
You need to create POM.xml which has all dependencies to run this maven project,
So for building the project you need to run `mvn install` which will download all the
dependencies written in pom.xml.
It will create a target folder and inside this there is a jar file. This Jar file contains all your code with required dependencies.

The dependencies for maven project is store in `pom.xml` file

Note - In both projects (Gradle or Maven) You will get a jar file and if you want to run this jar file then you need to execute a command "java -jar <your jar file>"

To Upload Maven Artifects to Nexus
======================================

Create a file inside your window directory c/user/nksaini/.m2 => settings.xml and add this there

```
<settings>
    <servers>
        <server>
            <id>nexus-snapshots</id>   # Nexus Profile Name
            <username>nirdesh</username>  # Nexus User Credentials
            <password>Nirdesh@123</password>
        </server>
    </servers>
</settings>
```

Add this to your pom.xml file under build

```

        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-deploy-plugin</artifactId>
                    <version>2.8.2</version>
                </plugin>
            </plugins>
        </pluginManagement>

        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-deploy-plugin</artifactId>
            </plugin>
        </plugins>

```

Add this to your pom.xml under project

```

    <distributionManagement>
        <snapshotRepository>
            <id>nexus-snapshots</id> # Nexus Creds Profile Refrence
            <url>http://13.232.237.54:8081/repository/maven-snapshots/</url> # Nexus Repo Url
        </snapshotRepository>
    </distributionManagement>

```

Build the jar - `mvn package`
Publish to Nexus - `mvn deploy`