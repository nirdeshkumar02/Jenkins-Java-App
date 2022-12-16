// -------------------------------------------------------- using external groovy scrit in Jenkinsfile -----------------------------------------------------------

//  External Groovy script (script.groovy) is helpfull to minimize the jenkinsfile, if you define all steps here jenkinsfile will have complexity

def gv

pipeline {
    agent any
    parameters {
        choice(name: 'VERSION', choices: ['1.1.0', '1.2.0', '1.3.0'], description: '')
        booleanParam(name: 'executeTests', defaultValue: true, description: '')
    }
    stages {
        stage("init") {
            steps {
                script {
                   gv = load "script.groovy"
                }
            }
        }
        stage("build") {
            steps {
                script {
                    gv.buildApp()
                }
            }
        }
        stage("test") {
            when {
                expression {
                    params.executeTests
                }
            }
            steps {
                script {
                    gv.testApp()
                }
            }
        }
        stage("deploy") {
            steps {
                script {
                    env.ENV = input message: "Select the environment to deploy to", ok: "Done", parameters: [choice(name: 'ONE', choices: ['dev', 'staging', 'prod'], description: '')]

                    gv.deployApp()
                    echo "Deploying to ${ENV}"
                }
            }
        }
    }
}

// -------------------------------------------------------- Syntexed Jenkinsfile -----------------------------------------------------------

// Using this 1st you need to click on build to update the jenkins server, then you will able to see option build with parameters.

// pipeline {
//     agent any

//     parameters {
//         choice(name: 'VERSION', choices: ['1.1.0', '1.2.0', '1.3.0'], description: '')
//         booleanParam(name: 'executeTests', defaultValue: true, description: '')
//     }

//     stages {
//         stage('build') {
//             steps {
//                 script {
//                     echo "Building the application..."
//                 }
//             }
//         }
//         stage('test') {
//             when {
//                 expression {
//                     params.executeTests
//                 }
//             }
//             steps {
//                 script {
//                     echo "Testing the application..."
//                 }
//             }
//         }
//         stage('deploy') {
//             steps {
//                 script {
//                     echo "Deploying the application..."
//                     echo "Deploying the version ${params.VERSION}"
//                 }
//             }
//         }
//     }
// }

// -------------------------------------------------------- Basic Jenkinsfile -----------------------------------------------------------
// pipeline {
//     agent none
//     stages {
//         stage('build') {
//             steps {
//                 script {
//                     echo "Building the application..."
//                 }
//             }
//         }
//         stage('test') {
//             steps {
//                 script {
//                     echo "Testing the application..."
//                 }
//             }
//         }
//         stage('deploy') {
//             steps {
//                 script {
//                     echo "Deploying the application..."
//                 }
//             }
//         }
//     }
// }
