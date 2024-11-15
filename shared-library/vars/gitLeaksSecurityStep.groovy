def call() {
    stage('GitLeaks Security') {
        steps {
            script {
                echo "Running GitLeaks security scan for sensitive data"
                sh '''
                    gitLeaks detect --source=. --config .gitleaks.toml
                '''
            }
        }
    }
}
