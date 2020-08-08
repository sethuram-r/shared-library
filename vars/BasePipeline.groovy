def call(String name){
    pipeline {
    agent any

    environment{
        REPO_NAME = "${name}"
    }

    tools {
        maven "maven 3.5.2"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Tools Check') {
            steps {
                sh 'mvn -v'
            }
        }
        stage('Build Package') {
            steps {
                sh 'mvn clean install -DskipTests -Pproduction'
            }
        }
        stage('Build and push Docker Image') {
            steps {
                script {
                    docker.withRegistry("https://registry.hub.docker.com", "dockerhub") {
                        def image = docker.build("sethuram975351/${REPO_NAME}:latest")
                        image.push()
                    }
                }
            }
        }
        stage('Build Kubernetes Deployment') {
            steps {
                dir("/Users/sethuram/Desktop/terraform/k8s") {
                    sh 'kubectl apply -f "${REPO_NAME}"-deployment.yaml'
                }

            }
        }
    }
}
}