pipeline {
    agent any
    environment {
        BRANCH = "${env.BRANCH_NAME}"
        DOCKER_HOST = 'tcp://host.docker.internal:2375'
    }

    tools {
        nodejs '7.8.0'
    }


    stages {
        stage('Docker Version') {
            steps {
                sh 'docker version'
            }
        }
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Set Variables, Change Logo') {
            steps {
                script {
                    if (env.BRANCH == 'main') {
                        env.PORT = '3000'
                        env.IMAGE_NAME = 'nodemain:v1.0'
                        logo_src = 'src/logo-main.svg'
                    } else {
                        env.PORT = '3001'
                        env.IMAGE_NAME = 'nodedev:v1.0'
                        logo_src = 'src/logo-dev.svg'
                    }
                    sh "cp ${logo_src} src/logo.svg"
                }
            }
        }
    }

        stage('Build') {
            steps {
                sh 'npm install'
            }
        }

        stage('Test') {
            steps {
                sh 'npm test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t $IMAGE_NAME ."
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'USER',
                    passwordVariable: 'PASS'
                )]) {
                    sh """
                        echo "$PASS" | docker login -u "$USER" --password-stdin
                        docker tag $IMAGE_NAME $USER/$IMAGE_NAME
                        docker push $USER/$IMAGE_NAME
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                sh """
                    docker stop $BRANCH || true
                    docker rm $BRANCH || true
                    docker run -d -p $PORT:$PORT --name $BRANCH $IMAGE_NAME
                """
            }
        }
}