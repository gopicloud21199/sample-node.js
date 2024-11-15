def call() {
    stage('Checkout Code') {
        steps {
            // Get the latest code from your version control system
            checkout scm
        }
    }
}
