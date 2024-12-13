def call(Map config = [:]) {
    def dockerfilePath = config.dockerfilePath ?: '.'
    def imageTag = config.imageTag ?: 'latest'
    def repository = config.repository ?: error("Parameter 'repository' is required.")
    def credentialsId = config.credentialsId ?: error("Parameter 'credentialsId' is required.")
    
    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'USERNAME', passwordVariable: 'TOKEN')]) {
        echo "Building Docker Image"
        sh """
            docker build -t ghcr.io/${USERNAME}/${repository}:${imageTag} ${dockerfilePath}
        """
    }
}
