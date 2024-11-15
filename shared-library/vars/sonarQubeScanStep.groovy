def call(String projectKey) {
    script {
        def scannerHome = tool 'allen-sonar'
        withSonarQubeEnv(credentialsId: 'sonarqube-token', installationName: 'allen-sonar') {
            sh """sonar-scanner \\ 
                -Dsonar.projectKey=${projectKey} \\ 
                -Dsonar.verbose=true \\ 
                -Dsonar.sources=. \\ 
                -Dsonar.projectBaseDir=. \\ 
                -Dsonar.exclusions=**/.github/**,**/cmd/**,**/server/**,**/mocks/**,**/*aws-template.yml,**/*mock*.go,**/tests/**,**/*_test.go,***/*_test_data.go,.yml,*.yaml,*.proto,Dockerfile,*.md,*.yaml,**/*.pb.go,**/*.pb.*.go,*.mod,*.json,*.out,Makefile,LICENSE,.gitignore \\ 
                -Dsonar.tests=. \\ 
                -Dsonar.test.inclusions=**/*_test*.go \\ 
                -Dsonar.go.coverage.reportPaths=sonar_coverage.out \\ 
                -Dsonar.go.tests.reportPaths=coverage.out"""
        }
        
        sh 'echo "Sonarqube scan is successful" > $WORKSPACE/sonarqube_scan_result.txt'
    }
}