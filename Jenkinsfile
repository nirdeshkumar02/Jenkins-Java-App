#!/usr/bin/env groovy

// Now We are using script.groovy and jenkins shared library together. Just focus on the code written here

// Importing Library which is globally available to Jenkins -----------------------

// @Library('Jenkins-Shared-Library')_

// Importing Jenkins Shared Library Directly to Jenkinsfile -----------------------

library identifier: 'Jenkins-Shared-Library@master', retriever: modernSCM(
    [$class: 'GitSCMSource',
    remote: 'https://github.com/nirdeshkumar02/Jenkins-Shared-Library.git',
    credentialsId: 'github-creds'
    ]
)

def gv

pipeline {
    agent any
    tools {
        maven 'Maven'
    }
    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage("build jar") {
            steps {
                script {
                    buildJar()
                }
            }
        }
        stage("build and push image") {
            steps {
                script {
                    // buildImage()
                    buildImage 'nirdeshkumar02/jenkins-java-app:jma-6'
                    dockerLogin()
                    dockerPush 'nirdeshkumar02/jenkins-java-app:jma-6'
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    gv.deployApp()
                }
            }
        }
    }
}
