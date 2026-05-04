pipeline {
    agent any

    environment {
        DOCKERHUB_USER = 'bahajyyilmaz'
        IMAGE_TAG      = "${env.GIT_COMMIT[0..6]}"
        PRODUCER_IMAGE = "${DOCKERHUB_USER}/producer-api:${IMAGE_TAG}"
        WORKER_IMAGE   = "${DOCKERHUB_USER}/worker-service:${IMAGE_TAG}"
    }

    tools {
        // Jenkins downloads this Maven version automatically via Maven Integration Plugin
        maven 'maven-3.9.6'
    }

    stages {
        stage('Build & Test') {
            steps {
                sh 'mvn clean verify -q'
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -f producer-api/Dockerfile -t ${PRODUCER_IMAGE} ."
                sh "docker build -f worker-service/Dockerfile -t ${WORKER_IMAGE} ."
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    sh "docker push ${PRODUCER_IMAGE}"
                    sh "docker push ${WORKER_IMAGE}"
                }
            }
        }
    }

    post {
        always {
            sh 'docker logout'
        }
        success {
            echo "Images pushed: ${PRODUCER_IMAGE} | ${WORKER_IMAGE}"
        }
        failure {
            echo 'Pipeline failed — no images were pushed.'
        }
    }
}
