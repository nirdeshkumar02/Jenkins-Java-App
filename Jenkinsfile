#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        maven 'Maven'
    }
    environment {
        ECR_REPO_URL = '626170306581.dkr.ecr.us-east-1.amazonaws.com'
        IMAGE_REPO = "${ECR_REPO_URL}/jenkins-java-app"
    }
    stages {
        stage('increment version') {
            steps {
                script {
                    echo 'incrementing app version...'
                    sh 'mvn build-helper:parse-version versions:set \
                        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                        versions:commit'
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
                }
            }
        }
        stage('build app') {
            steps {
                script {
                    echo "building the application..."
                    sh 'mvn clean package'
                }
            }
        }
        stage('build image') {
            steps {
                script {
                    echo "building the docker image..."
                    withCredentials([usernamePassword(credentialsId: '	ecr_creds', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh "docker build -t ${IMAGE_REPO}:${IMAGE_NAME} ."
                        sh "echo $PASS | docker login -u $USER --password-stdin ${ECR_REPO_URL}"
                        sh "docker push ${IMAGE_REPO}:${IMAGE_NAME}"
                    }
                }
            }
        }
        stage('deploy to eks') {
            environment {
                APP_NAME = 'jenkins-java-app'
                AWS_ACCESS_KEY = credentials('aws_access_key')
                AWS_SECRET_ACCESS_KEY = credentials('aws_secret_access_key')
            }
            steps {
                script {
                    echo "deploying docker image to aws eks"
                    sh 'envsubst < kubernetes/deployment.yaml | kubectl apply -f -' // envsubst is helpful to pass env vars to k8s yaml file
                    sh 'envsubst < kubernetes/service.yaml | kubectl apply -f -' // envsubst is helpful to pass env vars to k8s yaml file
                }
            }
        }
        stage('commit version update') {
            environment {
                GITHUB_TOKEN = credentials('github_token')
            }
            steps {
                script {
                        // git config here for the first time run
                        sh 'git config --global user.email "jenkins@example.com"'
                        sh 'git config --global user.name "jenkins"'

                        sh "git remote set-url origin https://${GITHUB_TOKEN}@github.com/nirdeshkumar02/Jenkins-Java-App.git"
                        sh 'git add .'
                        sh 'git commit -m "ci: version bump"'
                        sh 'git push origin HEAD:deploy-to-eks-pipeline'
                }
            }
        }
    }
}
