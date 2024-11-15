def call() {
    stage('Test') {
        steps {
            script {
                echo "Running Unit Tests"
                sh '''
                    # Run unit tests here (example: using Go, Java, Node.js, etc.)
                    # Example for Go:
                    go test -v ./...
                    '''
            }
        }
    }
}
