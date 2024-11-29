@Library('My_shared_library') _

pipeline {
    agent {
        docker {
            alwaysPull true
            image 'node:16-alpine'
            // registryUrl 'https://ap-south-1.console.aws.amazon.com/ecr/public-registry?region=ap-south-1'
            // registryCredentialsId 'ecr:ap-south-1:AWSKey'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }

    environment {
           REPOSITORY = 'public.ecr.aws/w9z3v9t4/my-sample-test'
           IMAGE_TAG = "${env.BUILD_NUMBER}"
           DOCKER_IMAGE = "${env.ECR_REPO}:${env.IMAGE_TAG}"
           REGISTRY = 'https://ap-south-1.console.aws.amazon.com/ecr/public-registry?region=ap-south-1'
           AWS_REGION = 'ap-south-1'
        // SONAR_PROJECT_KEY = 'your-sonarqube-project-key'
        // SONAR_HOST_URL = 'https://your-sonarqube-instance.com'
       // SONAR_LOGIN = credentials('sonar-token') // Jenkins credentials ID for SonarQube token
    }
    stages {
        stage('Checkout Code') {
            steps {
	       script {
                  Checkout()  // Reusable checkout code step from vars/checkoutCode.groovy
	      }
            }
        }

        stage('GitLeaks Security') {
            steps {
		script {
                   gitLeaksSecurityStep()  // Reusable GitLeaks security scan step
		 }
            }
        }

        stage('Test') {
            steps {
		script {
                  testStep()  // Reusable unit test step
		}
            }
        }

        stage('Build Package') {
            steps { 
		 script {
                   buildPackageStep()  // Reusable build package step (for Maven, Node.js)
		}
            }
        }

        stage('SonarQube Scan') {
            steps {
		 script {
                   sonarQubeScanStep('my-project-key')  // Reusable SonarQube scan step (pass the project key)
		}
            }
        }

        stage('Build Docker Image') {
            steps {
	        script {
                   buildDockerImageStep(buildArg: '--build-arg GIT_TOKEN=\$GITHUB_PAT')  // Reusable Docker build step
		}
            }
        }

        stage('Docker Vulnerability Scan') {
            steps 
		script {
                  dockerVulnerabilityScanStep()  // Reusable Docker vulnerability scan step
		}
            }
        }

        stage('Push to ECR') {
            steps {
		script {
                 dockerPushToEcrStep()  // Reusable Docker push to ECR step
		}
            }
        }

        stage('Update GitHub Repo') {
            steps {
		script {
                  updateImageInGithubStep() // Call the shared library function
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
