Jenkins
===========

Jenkins is a CICD Tools which helps you to perform CI and CD Operations in a efficient manner.
You can configure any tools which is required to your application can directly attach to jenkins via Plugin and download to jenkins server.

#### If you want to configure any plugin for global uses
`Jenkins UI -> Manage Jenkins -> Global Tool Configuration`

#### If you want to download any plugin 
`Jenkins UI -> Manage Jenkins -> Manage Plugin`

#### If you want to configure any tool to jenkins server
1. Login to Your Jenkins Server as root user. If you are using jenkins as docker container, then run this
    `docker exec -it -u 0 <container-name> bash` 
2. Identify the Linux used by jenkins using
    `cat /etc/os-release` or `cat /etc/issue`
3. Download Required Tools that you want to configure to jenkins using applicable package manager like
    `apt install <package>` or `yum install <package>` or any other method you want to use.
4. Download Required Plugin on Jenkins so Go To `Jenkins UI -> Manage Jenkins -> Manage Plugin`
    For use them in your project. First Configure the plugins under `Jenkins UI -> Manage Jenkins -> Global Tool Configuration`

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
