def call() {
    stage('Checkout Code') {
        steps {
            // Get the latest code from your version control system
            git branch: 'main', url: 'https://github.com/gopicloud21199/sample-node.js.git'
        }
    }
}
