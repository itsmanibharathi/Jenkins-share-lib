def call(Map config = [:]) {
    // Mandatory parameters
    def awsRegion = config.awsRegion ?: error("Parameter 'awsRegion' is required.")
    def ecrRepository = config.ecrRepository ?: error("Parameter 'ecrRepository' is required.")
    def dockerfilePath = config.dockerfilePath ?: '.'
    def imageTag = config.imageTag ?: 'latest'

    pipeline {
        agent any
        environment {
            AWS_REGION = awsRegion
        }
        stages {
            stage('Login to ECR') {
                steps {
                    script {
                        echo "Logging into AWS ECR"
                        sh """
                            aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com
                        """
                    }
                }
            }
            stage('Build Docker Image') {
                steps {
                    script {
                        echo "Building Docker Image"
                        sh """
                            docker build -t ${ecrRepository}:${imageTag} ${dockerfilePath}
                        """
                    }
                }
            }
            stage('Tag Docker Image') {
                steps {
                    script {
                        echo "Tagging Docker Image"
                        sh """
                            docker tag ${ecrRepository}:${imageTag} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ecrRepository}:${imageTag}
                        """
                    }
                }
            }
            stage('Push Docker Image to ECR') {
                steps {
                    script {
                        echo "Pushing Docker Image to ECR"
                        sh """
                            docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ecrRepository}:${imageTag}
                        """
                    }
                }
            }
        }
    }
}
