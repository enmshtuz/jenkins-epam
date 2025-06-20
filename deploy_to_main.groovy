pipeline {
    agent any

    environment {
        DOCKER_HOST = 'tcp://host.docker.internal:2375'
        IMAGE = 'nodemain:v1.0'
    }

    stages {
        stage('Pull and Run') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker pull $DOCKER_USER/$IMAGE

                        docker stop dev || true
                        docker rm dev || true

                        docker run -d -expose -p 3000:3000 --name dev $DOCKER_USER/$IMAGE
                    """
                }
            }
        }
    }
}
