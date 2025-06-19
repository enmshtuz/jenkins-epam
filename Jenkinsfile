pipeline {
    agent any
    environment {
        BRANCH = "${env.BRANCH_NAME}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Set Variables') {
            steps {
                script {
                    if (env.BRANCH == 'main') {
                        env.PORT = '3000'
                        env.IMAGE_NAME = 'nodemain:v1.0'
                        env.DEPLOY_JOB = 'Deploy_to_main'
                        env.LOGO_SRC = 'src/logo-main.svg'
                    } else {
                        env.PORT = '3001'
                        env.IMAGE_NAME = 'nodedev:v1.0'
                        env.DEPLOY_JOB = 'Deploy_to_dev'
                        env.LOGO_SRC = 'src/logo-dev.svg'
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

        stage('Replace Logo') {
            steps {
                sh "cp $LOGO_SRC src/logo.svg"
            }
        }