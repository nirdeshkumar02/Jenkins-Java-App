def buildJar() {
    echo "building the application..."
    sh 'mvn package'
} 

def buildImage() {
    echo "building the docker image..."
    withCredentials([usernamePassword(credentialsId: 'docker_creds', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh 'docker build -t nirdeshkumar02/jenkins-java-app:2.2.0 .'
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh 'docker push nirdeshkumar02/jenkins-java-app:2.2.0'
    }
} 

def deployApp() {
    echo 'deploying the application...'
} 

return this