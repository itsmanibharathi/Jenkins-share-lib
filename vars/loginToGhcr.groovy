def call(Map config = [:]) {
    def credentialsId = config.credentialsId ?: error("Parameter 'credentialsId' is required.")
    
    withCredentials([usernamePassword(credentialsId: credentialsId, usernameVariable: 'USERNAME', passwordVariable: 'TOKEN')]) {
        echo "Logging into GitHub Container Registry (GHCR)"
        sh """
            echo "${TOKEN}" | docker login ghcr.io -u "${USERNAME}" --password-stdin
        """
    }
}
