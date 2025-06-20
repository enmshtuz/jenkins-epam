pipeline {
    agent any
    environment {
        DOCKER_HOST = 'tcp://host.docker.internal:2375'
    }

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['main', 'dev'], description: 'Target environment to deploy to')
    }

    stages {
        stage('Set Variables') {
            steps {
                script {
                    if (params.ENVIRONMENT == 'main') {
                        env.IMAGE = 'nodemain:v1.0'
                        env.PORT = '3000'
                        env.CONTAINER = 'manual-main'
                    } else {
                        env.IMAGE = 'nodedev:v1.0'
                        env.PORT = '3001'
                        env.CONTAINER = 'manual-dev'
                    }
                }
            }
        }

        stage('Deploy Container') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker pull $DOCKER_USER/$IMAGE

                        docker stop $CONTAINER || true
                        docker rm $CONTAINER || true

                        docker run -d -p $PORT:3000 --name $CONTAINER -e HOST=0.0.0.0 $DOCKER_USER/$IMAGE
                    """
                }
            }
        }
    }
}
