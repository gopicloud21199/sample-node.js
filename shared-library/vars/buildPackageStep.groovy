def call() {
    stage('Build Package') {
        steps {
            script {
                echo "Building application package"
                sh '''
                    # Determine project type and build accordingly
                    if [ -f "pom.xml" ]; then
                        echo "Maven project detected. Building .jar/.war/.ear file."
                        # Run Maven to build the .jar/.war/.ear files (e.g., skipping tests for the build)
                        mvn clean package -DskipTests
                    elif [ -f "package.json" ]; then
                        echo "Node.js project detected. Building package."
                        # For Node.js, build the application and package it (e.g., create a .tar.gz of the build directory)
                        npm install
                        npm run build  # Assuming there is a build script defined in package.json
                        tar -czf myapp.tar.gz ./dist
                    else
                        echo "Unsupported project type. Skipping package build."
                    fi
                '''
            }
        }
    }
}