// Define las etapas del pipeline para Python
def call(Map config = [:]) {
    // Configuración
    def pythonPath = config.path ?: '.'
    def runSonar = config.runSonar == false
    def runDocker = config.runDocker == false
    def runCheckov = config.runCheckov == false
    def runPublishArtifact = config.runPublishArtifact == false
    
    pipeline {
        agent any
        // ETAPAS OBLIGATORIAS - Siempre se ejecutan
        stages{
            stage('Instalación de dependencias') {
                steps {
                    dir(pythonPath) {
                        sh 'python3 -m pip install --upgrade pip'
                        sh 'python3 -m pip install -r requirements.txt'
                    }
                }
            }
            
            stage('Pruebas unitarias') {
                steps {
                    dir(pythonPath) {
                        sh 'python3 -m pytest'
                    }
                }
            }
            
            // ETAPAS OPCIONALES - Se ejecutan según configuración
            if (runSonar) {
                stage('Análisis con SonarQube') {
                    steps {
                        dir(pythonPath) {
                            withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                                withSonarQubeEnv('SonarQubeServer') {
                                    sh 'sonar-scanner'
                                }
                            }
                        }
                    }
                }
                
                stage('Quality Gate') {
                    steps {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    }
                }
            }
            
            if (runCheckov) {
                stage('Análisis con Checkov') {
                    steps {
                        dir(pythonPath) {
                            sh 'python3 -m pip install checkov'
                            sh 'python3 -m checkov -d .'
                        }
                    }
                }
            }
            
            if (runPublishArtifact) {
                stage('Publicación de artefactos') {
                    steps {
                        dir(pythonPath) {
                            archiveArtifacts artifacts: 'dist/*.whl', fingerprint: true
                        }
                    }
                }
            }
            
            if (runDocker) {
                stage('Docker Build & Push') {
                    steps {
                        dir(pythonPath) {
                            withCredentials([usernamePassword(credentialsId: 'docker-registry', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
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
        }
    }
}
