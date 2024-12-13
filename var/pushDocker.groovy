def call(Map config = [:]) {
    def imageTag = config.imageTag ?: 'latest'
    def repository = config.repository ?: error("Parameter 'repository' is required.")
    def credentialsId = config.credentialsId ?: error("Parameter 'credentialsId' is required.")
    
    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'USERNAME', passwordVariable: 'TOKEN')]) {
        echo "Pushing Docker Image"
        sh """
            echo "${TOKEN}" | docker login ghcr.io -u "${USERNAME}" --password-stdin
            docker push ghcr.io/${USERNAME}/${repository}:${imageTag}
        """
    }
}
