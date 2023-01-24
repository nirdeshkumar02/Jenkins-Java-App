pipeline {
    agent any
    environment {
        ANSIBLE_SERVER = "167.99.136.157" // Ipv4 address of ansible server
    }
    stages {
        stage("copy files to ansible server") {
            steps {
                script {
                    echo "copying all neccessary files to ansible control node"
                    // Copy file from local to ansible server
                    sshagent(['ansible_server_key']) {
                        sh "scp -o StrictHostKeyChecking=no ansible/* root@${ANSIBLE_SERVER}:/root"
                        // Copy ec2 servers private ssh key from jenkins creds
                        withCredentials([sshUserPrivateKey(credentialsId: 'ec2_server_key', keyFileVariable: 'keyfile', usernameVariable: 'user')]) {
                            sh 'scp $keyfile root@$ANSIBLE_SERVER:/root/ssh-key.pem'
                        }
                    }
                }
            }
        }
        stage("execute ansible playbook") {
            steps {
                script {
                    echo "calling ansible playbook to configure ec2 instances"
                    // Run commands on remote ec2 servers
                    def remote = [:]
                    remote.name = "ansible-server"
                    remote.host = ANSIBLE_SERVER
                    remote.allowAnyHosts = true

                    withCredentials([sshUserPrivateKey(credentialsId: 'ansible_server_key', keyFileVariable: 'keyfile', usernameVariable: 'user')]){
                        remote.user = user
                        remote.identityFile = keyfile
                        sshScript remote: remote, script: "prepare-ansible-server.sh"
                        sshCommand remote: remote, command: "ansible-playbook my-playbook.yaml"
                    }
                }
            }
        }
    }   
}