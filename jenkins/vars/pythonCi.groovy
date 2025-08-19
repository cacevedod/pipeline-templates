def call(Map config = [:]) {
    // Valores por defecto para features opcionales
    def runSonar = config.runSonar == true
    def runDocker = config.runDocker == true
    def runCheckov = config.runCheckov == true
    def runPublishArtifact = config.runPublishArtifact == true
    def runTests = config.runTests != false
    def pythonPath = config.path ?: '.'
    dir(pythonPath) {
        stage('Checkout') {
            checkout scm
        }
        stage('Install dependencies') {
            sh 'python -m pip install --upgrade pip'
            sh 'pip install -r requirements.txt'
        }
        if (runTests) {
            stage('Unit Test') {
                sh 'pytest'
            }
        }
        if (runSonar) {
            withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                stage('SonarQube Analysis') {
                    withSonarQubeEnv('SonarQubeServer') {
                        sh 'sonar-scanner'
                    }
                }
                stage('Quality Gate') {
                    timeout(time: 5, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }
        }
        if (runCheckov) {
            stage('Checkov') {
                sh 'pip install checkov'
                sh 'checkov -d .'
            }
        }
        if (runPublishArtifact) {
            stage('Publish Artifact') {
                archiveArtifacts artifacts: 'dist/*.whl', fingerprint: true
            }
        }
        if (runDocker) {
            withCredentials([usernamePassword(credentialsId: 'docker-registry', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                stage('Build & Push Docker Image') {
                    script {
                        def imageName = "${DOCKER_USERNAME}/${env.JOB_NAME}:${env.BUILD_NUMBER}"
                        def dockerImage = docker.build(imageName)
                        docker.withRegistry('', 'docker-registry') {
                            dockerImage.push()
                        }
                    }
                }
            }
        }
    }
}
