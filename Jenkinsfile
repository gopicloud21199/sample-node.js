@Library('shared-library') _

pipeline {
    agent {
        docker {
            alwaysPull true
            image '537984406465.dkr.ecr.ap-south-1.amazonaws.com/allen-jenkins-agent:latest'
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
                Checkout()  // Call the shared library step for code checkout
            }
        }
        
        stage('GitLeaks Security') {
            steps {
                gitLeaksSecurityStep()  // Run GitLeaks security scan for sensitive data
            }
        }
        
        stage('Test') {
            steps {
                testStep()  // Run unit tests
            }
        }

        stage('Build Package') {
            steps {
                buildPackageStep()  // Build the application package (Maven, Node.js, etc.)
            }
        }

        stage('SonarQube Scan') {
            steps {
                script {
                    sonarQubeScanStep('your-project-key')  // Run SonarQube analysis
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                buildDockerImageStep(buildArg: '--build-arg GIT_TOKEN=\$GITHUB_PAT')  // Build the Docker image
            }
        }

        stage('Docker Vulnerability Scan') {
            steps {
                dockerVulnerabilityScanStep()  // Run Trivy to scan Docker image for vulnerabilities
            }
        }

        stage('Push to ECR') {
            steps {
                dockerPushToEcrStep()  // Push the built Docker image to AWS ECR
            }
        }

        stage('Update GitHub Repo') {
            steps {
                updateImageInGithubStep() // Call the shared library function
            }
        }
    }

    post {
        always {
            echo 'Cleaning up...'
            cleanWs()  // Clean up the workspace after the build
        }
    }
}
