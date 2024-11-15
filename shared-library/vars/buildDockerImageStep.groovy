def call(Map args) {
    script {
        def buildArg = args.buildArg ?: '--build-arg GIT_TOKEN=\$GITHUB_PAT'
        
        echo 'Building Docker Image'
        withCredentials([string(credentialsId: 'git_pat', variable: 'GITHUB_PAT')]) {
            sh """
                # Fetching the latest commit ID
                echo 'Fetching the latest commit ID...'
                git config --global --add safe.directory '*'
                commitId=\$(git log -n1 --format='%h')
                date=\$(date -u +'_%Y_%m_%d_%H_%M')
                IMAGE_TAG=v_\${commitId}\${date}
                echo 'Generated IMAGE_TAG: \$IMAGE_TAG'

                # Create a file to store the IMAGE_TAG
                touch ~/IMAGE_TAG.txt && echo \$IMAGE_TAG > ~/IMAGE_TAG.txt

                # Display the generated image and tag
                echo 'Image and tag to be built: \$REGISTRY/\$REPOSITORY:\$IMAGE_TAG'

                # Explicitly specify the build argument
                echo 'Building Docker image...'
                docker build ${buildArg} -t \$REGISTRY/\$REPOSITORY:\$IMAGE_TAG .
            """
        }
    }
}