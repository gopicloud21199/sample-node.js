pipeline {
    agent {
        docker {
            alwaysPull true
            image '577638354424.dkr.ecr.ap-south-1.amazonaws.com/sample-nodejs-app:latest'
            registryUrl 'https://577638354424.dkr.ecr.ap-south-1.amazonaws.com'
            registryCredentialsId 'ecr:ap-south-1:AWSKey'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
         ECR_REPO = '577638354424.dkr.ecr.ap-south-1.amazonaws.com/my-sample-repo'
         IMAGE_TAG = "${env.BUILD_NUMBER}"
         DOCKER_IMAGE = "${env.ECR_REPO}:${env.IMAGE_TAG}"
         // SONAR_PROJECT_KEY = 'your-sonarqube-project-key'
         // SONAR_HOST_URL = 'https://your-sonarqube-instance.com'
         // SONAR_LOGIN = credentials('sonar-token') // Jenkins credentials ID for SonarQube token
    }
    stages {
        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/gopicloud21199/sample-node.js.git'
            }
        }

        stage('GitLeaks Security') {
            steps {
                echo "Running GitLeaks security scan for sensitive data"
                sh '''
                    gitLeaks detect --source=. --config .gitleaks.toml
                '''
            }
        }

        stage('Test') {
            steps {
                echo "Running Unit Tests"
                sh '''
                    # Run unit tests here (example: using Go, Java, Node.js, etc.)
                    # Example for Go:
                    go test -v ./...
                '''
            }
        }

        stage('Build Package') {
            steps { 
                echo "Building application package"
                sh '''
                    # Determine project type and build accordingly
                    if [ -f "pom.xml" ]; then
                        echo "Maven project detected. Building .jar/.war/.ear file."
                        mvn clean package -DskipTests
                    elif [ -f "package.json" ]; then
                        echo "Node.js project detected. Building package."
                        npm install
                        npm run build  # Assuming there is a build script defined in package.json
                        tar -czf myapp.tar.gz ./dist
                    else
                        echo "Unsupported project type. Skipping package build."
                    fi
                '''
            }
        }
    /*
        stage('SonarQube Scan') {
            steps {
                def scannerHome = tool 'sonar'
                withSonarQubeEnv(credentialsId: 'sonarqube-token', installationName: 'sonar') {
                    sh """ 
                        sonar-scanner \\
                        -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} \\
                        -Dsonar.verbose=true \\
                        -Dsonar.sources=. \\
                        -Dsonar.projectBaseDir=. \\
                        -Dsonar.exclusions=**/.github/**,**/cmd/**,**/server/**,**/mocks/**,**/*aws-template.yml,**/*mock*.go,**/tests/**,**/*_test.go,**/*_test_data.go,**/*.yml,**/*.yaml,**/*.proto,Dockerfile,**/*.md,**/*.yaml,**/*.pb.go,**/*.pb.*.go,**/*.mod,**/*.json,**/*.out,Makefile,LICENSE,.gitignore \\
                        -Dsonar.tests=. \\
                        -Dsonar.test.inclusions=**/*_test*.go \\
                        -Dsonar.go.coverage.reportPaths=sonar_coverage.out \\
                        -Dsonar.go.tests.reportPaths=coverage.out
                    """
                }
                sh 'echo "Sonarqube scan is successful" > $WORKSPACE/sonarqube_scan_result.txt'
            }
        }
   */
        stage('Build Docker Image') {
            steps {
                echo 'Building Docker Image'
                withCredentials([string(credentialsId: 'git_pat', variable: 'GITHUB_PAT')]) {
                    sh """
                        # Fetching the latest commit ID
                        commitId=\$(git log -n1 --format='%h')
                        date=\$(date -u +'_%Y_%m_%d_%H_%M')
                        IMAGE_TAG=v_\${commitId}\${date}
                        echo 'Generated IMAGE_TAG: \$IMAGE_TAG'

                        # Create a file to store the IMAGE_TAG
                        touch ~/IMAGE_TAG.txt && echo \$IMAGE_TAG > ~/IMAGE_TAG.txt

                        # Build the Docker image
                        docker build -t \$REGISTRY/\$REPOSITORY:\$IMAGE_TAG .
                    """
                }
            }
        }

        stage('Docker Vulnerability Scan') {
            steps {
                sh '''
                    IMAGE_TAG=`cat ~/IMAGE_TAG.txt`
                    trivy image -s HIGH,CRITICAL -q \$REGISTRY/\$REPOSITORY:\$IMAGE_TAG
                    trivy image -s HIGH,CRITICAL -q --format json \$REGISTRY/\$REPOSITORY:\$IMAGE_TAG > report.json

                    critical_count=$(jq -r '.Results[].Vulnerabilities | map(select(.Severity == "CRITICAL")) | length' "report.json")
                    high_count=$(jq -r '.Results[].Vulnerabilities | map(select(.Severity == "HIGH")) | length' "report.json")
                    
                    echo "{\"high_count\": \"$high_count\", \"critical_count\": \"$critical_count\"}" > image_vulnerability_count_report.json
                    trivy image -s HIGH,CRITICAL -q --format template --template "@html.tpl" \$REGISTRY/\$REPOSITORY:\$IMAGE_TAG > report.html

                    # Check if the branch is "main" before sending to S3
                    if [ "$BRANCH_NAME" = "main" ]; then
                        aws s3 cp report.html s3://scansite.allen-demo.in/${serviceName}/container-image-scan/report.html
                        aws s3 cp image_vulnerability_count_report.json s3://scansite.allen-demo.in/${serviceName}/container-image-scan/image_vulnerability_count_report.json
                    else
                        echo "Branch is not main. Skipping S3 upload."
                    fi
                '''
            }
        }

        stage('Push to ECR') {
            steps {
                sh '''
                    IMAGE_TAG=`cat ~/IMAGE_TAG.txt`  
                    aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 537984406465.dkr.ecr.ap-south-1.amazonaws.com
                    docker push $REGISTRY/$REPOSITORY:$IMAGE_TAG
                '''
            }
        }

        stage('Update GitHub Repo') {
            steps {
                echo "Updating GitHub repository with the new image tag"
        
                def imageTag = sh(script: 'cat ~/IMAGE_TAG.txt', returnStdout: true).trim()
                def repoUrl = "https://github.com/your-username/your-repository.git"
                def branch = "main"
        
                sh """
                    git clone ${repoUrl} repo
                    cd repo
                    echo "Latest Docker Image Tag: ${imageTag}" > docker_image_tag.txt
                    git add docker_image_tag.txt
                    git config user.name "Jenkins CI"
                    git config user.email "jenkins@example.com"
                    git commit -m "Update Docker image tag to ${imageTag}"
                    git push origin ${branch}
                """
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully'
        }
        failure {
            echo 'Pipeline failed'
        }
    }
}
