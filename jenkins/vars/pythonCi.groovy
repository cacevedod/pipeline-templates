def call(Map config = [:]) {
    pipeline {
        agent any
        environment {
            SONAR_TOKEN = credentials('sonar-token')
            DOCKER_REGISTRY = credentials('docker-registry')
        }
        stages {
            stage('Checkout') {
                steps {
                    checkout scm
                }
            }
            stage('Install dependencies') {
                steps {
                    sh 'python -m pip install --upgrade pip'
                    sh 'pip install -r requirements.txt'
                }
            }
            stage('Unit Test') {
                when {
                    expression { config.runTests != false }
                }
                steps {
                    sh 'pytest'
                }
            }
            stage('SonarQube Analysis') {
                when {
                    expression { env.SONAR_TOKEN }
                }
                steps {
                    withSonarQubeEnv('SonarQubeServer') {
                        sh 'sonar-scanner'
                    }
                }
            }
            stage('Quality Gate') {
                when {
                    expression { env.SONAR_TOKEN }
                }
                steps {
                    timeout(time: 5, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }
            stage('Checkov') {
                steps {
                    sh 'pip install checkov'
                    sh 'checkov -d .'
                }
            }
            stage('Publish Artifact') {
                steps {
                    archiveArtifacts artifacts: 'dist/*.whl', fingerprint: true
                }
            }
            stage('Build & Push Docker Image') {
                when {
                    expression { env.DOCKER_REGISTRY }
                }
                steps {
                    script {
                        dockerImage = docker.build("${DOCKER_REGISTRY}/${env.JOB_NAME}:${env.BUILD_NUMBER}")
                        docker.withRegistry('', 'docker-registry') {
                            dockerImage.push()
                        }
                    }
                }
            }
        }
    }
}
