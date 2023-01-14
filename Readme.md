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
2. Make It available to jenkins so that you can retreive it to multiple pipeline at locaton.
    `Jenkins UI -> Manage Jenkins -> Manage System ->  Global Pipeline Libraries`
3. Now Use Shared Library to Jenkinsfile by importing.
    `@Library('name given during adding jenkins shared library to jenkins')`
4. Or If You can directly use Jenkins Shared Libraries to Jenkinsfile without adding them to Jenkins UI like. 
    ```
        library identifier: 'jenkins-shared-library@master', retriever: modernSCM(
        [$class: 'GitSCMSource',
         remote: 'https://github.com/nirdeshkumar02/Jenkins-Shared-Library.git',
         credentialsId: 'github-creds'
        ])
    ```

Trigger Build If Any Changes Push to repo Automatically
=========================================================

For GitHub Repositories

1. Install `GitHub Plugin` plugin on jenkins.
2. Go to `Manage Jenkins -> Configure System -> Locate GitHub` Section and `Add Github Server`.
3. Now do this modification -
    ```
    API URL - For Github.com - https://api.github.com, For Github Enterprise - https://ghe.acme.com/api/v3/
    Credential - Create Your Personal Access Token on Github.com with their scops usecases and Add them to jenkins.
    Test Connection
    ```
4. Personal Access Token can be created at location - github.com/settings/tokens/new
5. Configure Jenkins Jobs on which you want push trigger automatically.
    ```
    Create a pipeline project -> In General, Check Github project and add the repo url -> In SCM, Choose Git add repo and branch 
    -> In Build Trigger, Check Github hook trigger for GitSCM Polling.

    Now, Go to your Github Repo -> Setting -> Webhook -> Add the webhook as Url will be Jenkins URL, Secret will be empty and Choose the scope.
    ```
6. Apply and save

Automatic Virsioning in Jenkins
================================
Major.Minor.Patch
When You Release a new version of software or application then you need to increase the version according the type of release whether it is major, minor or patch.

#### To update the version of Maven Project, Use this command.

```
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion} versions:commit

Upgrade patch version then use - nextIncrementalVersion as used in above command
Upgrade Minor Version - nextminorVersion
```

Deployed New Version to Ec2
===============================

1. Download the plugin "SSH" on Jenkins Ui.
2. Create Global credential by using SSH Username and Private Key (Put Pem data to creds).
3. Now, Run Docker run command directly in jenkinsfile or use dockerfile.

Deploy Application to EKS Using Jenkins with Docker Hub
==========================================================
1. Create a folder where you can have your kubernetes configuration file.
    ```
    folder - kubernetes
    files - deployment.yaml, service.yaml
    ```
2. Install gettext-base package to the jenkins server so that you can pass your jenkinsfile envs to k8s yaml file.
    ```
    apt install gettext-base
    ```
3. Add AWS Creds of Jenkins User on Jenkins UI for AWS Account Authentication.
    ```
    Create an AWS IAM User as Jenkins User with limited policy.
    Download Jenkins AWS User Creds.
    Create Global Creds inside Jenkins UI. Dashboard -> Manage Server -> Manage Creds -> Global as Secret Text
    ```
4. Create AWS EKS Cluster with Node Group.
5. Inside Jenkins Server, Install aws cli, AWS IAM Authenticator and kubectl. 
    ```
    For aws-cli - https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html
    For aws-iam-authenticator - https://docs.aws.amazon.com/eks/latest/userguide/install-aws-iam-authenticator.html
    For kubectl - https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/
    ```
6. Configure Jenkins User aws creds and also configure your cluster as kubeconfig.
    ```
    Run command for aws-configure - aws configure
    For kubeconfig - https://docs.aws.amazon.com/eks/latest/userguide/create-kubeconfig.html

    to test run "kubectl get nodes"
    ```
7. Inside the JenkinsFile, Add an step for deployment to kubernetes.
8. Good Practice Code which add to above steps -
    ```
    stage('deploy') {
        environment {
            APP_NAME = 'jenkins-java-app'
            AWS_ACCESS_KEY = credentials('aws_access_key')  --- add this creds to jenkins as secret text
            AWS_SECRET_ACCESS_KEY = credentials('aws_secret_access_key') --- add this creds to jenkins as secret text
        }
        steps {
            script {
                echo "deploying docker image to aws eks"
                sh 'envsubst < kubernetes/deployment.yaml | kubectl apply -f -' 
                sh 'envsubst < kubernetes/service.yaml | kubectl apply -f -' 
            }
        }
    }
    ```
9. Create a secret for docker registry inside jenkins server so that it can access the image from private repo.
    ```
    kubectl create secret docker-registry <secretName used in deployment> --docker-server=docker.io --docker-username=<username> --docker-password=<password> 
    ```
10. Push this changes to github and Run the Jenkins Pipeline.

Deploy Application to EKS Using Jenkins with ECR
==========================================================
- All the steps are same as with pipeline "deploy-to-eks-pipeline". 
- Just change the steps of "build image", and respective changes to step "deploy to k8s" 

1. Create an Private ECR Repository on AWS.
2. Create Credential in Jenkins for ECR.
    ```
    To get Password, Run Command - aws ecr get-login-password --region <repository region>
    Go To Jenkins Server -> Manage Jenkins -> Manage Credential -> Add a global credential for ecr where -
        Username - AWS 
        Password - <text-from-above-command>
    ```
3. Create Secret for ECR in Jenkins Server as did with docker.
    ```
    kubectl create secret docker-registry aws-registry-key --docker-server=<"accountId".dkr.ecr.<repo-region>.amazonaws.com> --docker-username=AWS --docker-password=<text copied from step 2>
    ```
4. Add the above created secret inside k8s deployment file.
    ```
    imagePullSecrets:
    - name: aws-ecr-registry-key

    and
    image: ${IMAGE_REPO}:${IMAGE_NAME}
    ```
5. Update the JenkinsFile.


Terraform-AWS-Infra
=======================
1. For server Key-Pair to connect server
    ```
    - create a key pair on aws 
    - add it to the jenkins server -> Go to Jenkins Server -> create new creds -> ssh username and private key -> Give some ID
        -> username ec2-user -> add the data of pem file to private key -> done
    ```
2. Install Terraform inside Jenkins Server.
3. Create a terraform directory and add code which provision the required resource on aws
    ```
    Create S3 Bucket for remote backend with given name - "myapp-bucket"
    VPC and Its related component
    EC2 Instance and the state should be on remote
    Refrence -  Terraform folder inside branch "infra-provision-terraform"
    ```
4. In Jenkinsfile Create a step for provision server and add code
    ```
        stage('provision server') {
            environment {
                # Aws Credential used to deploy infra
                AWS_ACCESS_KEY_ID = credentials('aws_access_key') 
                AWS_SECRET_ACCESS_KEY = credentials('aws_secret_access_key')
                # Changing env prefix variable value used in terraform config
                TF_VAR_env_prefix = 'test'
            }
            steps {
                script {
                    dir('terraform') {
                        sh "terraform init"
                        sh "terraform apply --auto-approve"
                        EC2_PUBLIC_IP = sh(
                            # fetch server ip using tf output command
                            script: "terraform output ec2_public_ip",
                            returnStdout: true
                        ).trim()
                    }
                }
            }
        }
    ```
5. Again, In jenkinsfile Add the step to deploy application to server
    ```
        stage('deploy') {
            environment {
                # to login in server as docker image in private repo
                DOCKER_CREDS = credentials('docker_creds')
            }
            steps {
                script {
                   echo "waiting for EC2 server to initialize" 
                   sleep(time: 90, unit: "SECONDS") 

                   echo 'deploying docker image to EC2...'
                   echo "${EC2_PUBLIC_IP}"

                    # DOCKER_CREDS_USR and DOCKER_CREDS_PSW automatically fetched from DOCKER_CREDS of type username and password
                   def shellCmd = "bash ./server-cmds.sh ${IMAGE_NAME} ${DOCKER_CREDS_USR} ${DOCKER_CREDS_PSW}"
                   def ec2Instance = "ec2-user@${EC2_PUBLIC_IP}"

                   sshagent(['server-ssh-key']) {
                       sh "scp -o StrictHostKeyChecking=no server-cmds.sh ${ec2Instance}:/home/ec2-user"
                       sh "scp -o StrictHostKeyChecking=no docker-compose.yaml ${ec2Instance}:/home/ec2-user"
                       sh "ssh -o StrictHostKeyChecking=no ${ec2Instance} ${shellCmd}"
                   }
                }
            }
        }
    ```
6. To destroy terraform infra
    - If you are using remote backend as s3
        ```
        ```

    - if you are using local backend -as it is created through Jenkins so we don't have the state file in local.
        ```
        Using Jenkins pipeline with your build no.
        Click on Replay, At stage provisioning inside dir(terraform)
        replace the all command with single one "terraform destroy --auto-approve"
        Comment the next stage deploy to ec2
        Run the pipeline again
        ```


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


