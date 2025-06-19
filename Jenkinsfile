pipeline {
    agent any
    environment {
        BRANCH = "${env.BRANCH_NAME}"
        PORT = BRANCH == 'main' ? '3000' : '3001'
        IMAGE_NAME = BRANCH == 'main' ? 'nodemain:v1.0' : 'nodedev:v1.0'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
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
            steps 
                sh "docker build -t $IMAGE_NAME ."
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    sh """
                        echo $PASS | docker login -u $USER --password-stdin
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

        stage('Trigger Deploy Pipeline') {
            steps {
                build job: BRANCH == 'main' ? 'Deploy_to_main' : 'Deploy_to_dev'
            }

        
        stage('Replace Logo') {
            steps {
                script {
                    def logoSource = BRANCH == 'main' ? 'src/logo-main.svg' : 'src/logo-dev.svg'
                    sh "cp $logoSource /src/logo.svg"
                }
            }
        }
     }
}
