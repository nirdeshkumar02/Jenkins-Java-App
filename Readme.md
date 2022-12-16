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
    
FreeStyle project for test, build, dockerized, and pushed to docker hub the Java Application
=================================================================================================
1. Go To `Jenkins UI -> Dashboard -> New Item` Give a name to your job and select `FreeStyle Project` then Press Ok.

2. Go To `Source Code Management` Configure Git by providing "Repository Url" and "Branch".

3. Choose your vcs credential (gitlab, github), If not shown, You need to add your creds to jenkins first.

3. Go To `Build Step` and Add `Invoke top-level Maven targets`.

4. If above value not showing in dropdown of build step, then You need need to configure maven first under 
    `Jenkins UI -> Manage Jenkins -> Global Tool Configuration`.

5. Inside the Maven Build Step, Choose your maven and add "test" in goals.

6. Add Another build step of maven and now add "package" in goals.

7. Add Docker Hub Creds inside `Jenkins UI -> Manage Jenkins -> Manage Credential -> Stores scoped to Jenkins (domains) -> Add Credential`

8. Create Repository at Docker Hub to push the image inside docker hub repo.

9. Choose "Use secret text(s) or file(s)" Under `Build Environment` in Configure job and Add "USERNAME" and "PASSWORD" value to username variable, password variable respectively and choose docker credential value to credential.

10. Add Another Build Step of type "Execute shell" and inside command box type
    ```
    chmod +x freestyle-build.sh
    ./freestyle-build.sh
    docker build -t nirdeshkumar02/jenkins-java-app:jma-1 .
    docker login -u $USERNAME -p $PASSWORD 
    docker push nirdeshkumar02/jenkins-java-app:jma-1
    ```

11. Now, Apply and Save it.

12. Now, Click now Build Now and You will check output by click the job name on the same page.

13. Done FreeStyle project for test and build the Java Application.

Jenkinsfile Structure
=========================

```
CODE_CHANGES = getGitChanges() # callingfunction which checks code changes
pipeline {
    agent none
    # Aceess build tools - only 3 available gradle, maven and jdk, these have to pre configured in jenkins ui
    tools {
        maven/gradle/jdk 'Maven/Gradle'
    }
    parameters {
        string(name: "Version", defaultValue: "", description: "versiion to deploy")
        choice(name: "VERSION", choices: ["1.1.0", "1.3.0"], description: "versiion to deploy")
        booleanParam(name: "executeTests", defaultValue: true, description: "versiion to deploy")
    }
    environment {
        NEW_VERSION = '1.2.0'
        SERVER_CREDS = credentials('credential-id') # it will bind the jenkins creds to jenkinsfile. for that add "crdential binding plugin to jenkins"
    }
    stages {
        stage('build') {
            when {
                expressiion {
                    BRANCH_NAME == "dev" || CODE_CHANGES == true
                }
            }
            steps {
                script {
                    echo "Building version ${NEW_VERSION} the application..."
                }
            }
        }
        stage('test') {
            when {
                expressiion {
                    BRANCH_NAME == "dev" || BRANCH_NAME == 'master' || params.executeTests == true  # Refrencing Parameter
                }
            }
            steps {
                script {
                    echo "Testing the application..."
                }
            }
        }
        stage('deploy') {
            steps {
                script {
                    echo "Deploying the application..."

                    # This will helpful if you have to provide creds only in single stage.
                    or withCredentials ([
                        usernamePassword( credentials: 'credentialId', usernameVariable: USER, passwordVariable: PWD)
                    ]) {
                        sh "some script ${USER} ${PWD}"
                    }

                    # Defining in environment, mostly used when you have to use them in multi stage.
                    echo "Deploying with ${SERVER_CREDS}"
                    sh "{SERVER_CREDS}"

                    echo "Deploying version ${params.VERSION}" # Refrecing Parameter
                }
            }
        }
    }
    post {
        # Post conditions: always, success, failure
        always {
            task like sending email whethere build failed or success in every situation
        }
        success {
            do something when build success
        }
    }
}

```

Pipeline project for test, build, dockerized, and pushed to docker hub the Java Application
=================================================================================================

Pipeline Projects are Scripted so, unlike to configure all steps as in freestyle, 
    You need to write script for all the steps, you wanna add to your pipeline project.

1. Go To `Jenkins UI -> Dashboard -> New Item` Give a name to your job and select `Pipeline Project` then Press Ok.

2. Go To `Pipeline` Choose your definition to build Pipeline `Pipeline Script from SCM`.
    Pipeline Script - Gives You Possibility to write your script in Jenkins UI.
    Pipeline Script for SCM - You can provide your Jenkinsfile path located in your Source Code.
    If You don't know about how to write script then Choose Pipeline Syntax. 

3. Add your github repo url, branch and its credential to configure pipeline job. 

4. Apply and Save.

5. Build Now.

Jenkins Credential scope
===========================
1. Global (Jenkins nodes, items, all child item)
2. System (Jenkins and Nodes Only)

Jenkins Credential types
===========================
1. Username password
2. Secret Text
3. Secret file
4. SSH Username with Private key
5. Certificate

Jenkins Shared Library
===========================
Jenkins Shared Library is a way to integrate the common logic all over the pipelines just by giving refrence of shared library. 
It is an extension to pipeline and written in groovy.

1. Create a repository for jenkins shared library where you have common code.
2. Make It available to jenkins so that you can retreive it to multiple pipeline at locaton 
    `Jenkins UI -> Manage Jenkins -> Manage System ->  Global Pipeline Libraries`
3. Now Use Shared Library to Jenkinsfile by importing
    `@Library('name given during adding jenkins shared library to jenkins')`

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
