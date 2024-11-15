// vars/updateImageInGithubStep.groovy

def call() {
    script {
        echo "Updating GitHub repository with the new image tag"
        
        // Fetch the IMAGE_TAG from the file created during the Docker image build process
        def imageTag = sh(script: 'cat ~/IMAGE_TAG.txt', returnStdout: true).trim()

        // Define your GitHub repository details and the branch to update
        def repoUrl = "https://github.com/your-username/your-repository.git"
        def branch = "main"  // or any other branch you'd like to push to
        
        // Clone the repository
        sh """
            git clone ${repoUrl} repo
            cd repo

            # Create a file with the updated image tag, or modify an existing file
            echo "Latest Docker Image Tag: ${imageTag}" > docker_image_tag.txt
            git add docker_image_tag.txt

            # Commit and push changes
            git config user.name "Jenkins CI"
            git config user.email "jenkins@example.com"
            git commit -m "Update Docker image tag to ${imageTag}"
            git push origin ${branch}
        """
    }
}
