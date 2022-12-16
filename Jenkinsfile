// -------------------------------------------------------- Syntexed Jenkinsfile -----------------------------------------------------------
pipeline {
    agent any

    parameters {
        choice(name: 'VERSION', choices: ['1.1.0', '1.2.0', '1.3.0'], description: '')
        booleanParam(name: 'executeTests', defaultValue: true, description: '')
    }

    stages {
        stage('build') {
            steps {
                script {
                    echo "Building the application..."
                }
            }
        }
        stage('test') {
            when {
                expression {
                    params.executeTests
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
                    echo "Deploying the version ${params.VERSION}"
                }
            }
        }
    }
}

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
