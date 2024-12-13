def call(Map config = [:]) {
    // Mandatory parameters
    def ghcrRepository = config.ghcrRepository ?: error("Parameter 'ghcrRepository' is required.")
    def dockerfilePath = config.dockerfilePath ?: '.'
    def imageTag = config.imageTag ?: 'latest'
    def ghcrUsername = credentials('github-username') // Replace with Jenkins credential ID for GitHub username
    def ghcrToken = credentials('github-token')       // Replace with Jenkins credential ID for GitHub token

    pipeline {
        agent any
        environment {
            GHCR_USERNAME = ghcrUsername
            GHCR_TOKEN = ghcrToken
        }
        stages {
            stage('Login to GHCR') {
                steps {
                    script {
                        echo "Logging into GitHub Container Registry (GHCR)"
                        sh """
                            echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USERNAME" --password-stdin
                        """
                    }
                }
            }
            stage('Build Docker Image') {
                steps {
                    script {
                        echo "Building Docker Image"
                        sh """
                            docker build -t ghcr.io/${GHCR_USERNAME}/${ghcrRepository}:${imageTag} ${dockerfilePath}
                        """
                    }
                }
            }
            stage('Push Docker Image to GHCR') {
                steps {
                    script {
                        echo "Pushing Docker Image to GHCR"
                        sh """
                            docker push ghcr.io/${GHCR_USERNAME}/${ghcrRepository}:${imageTag}
                        """
                    }
                }
            }
        }
    }
}
