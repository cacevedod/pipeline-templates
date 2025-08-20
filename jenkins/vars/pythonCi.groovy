// Define las etapas del pipeline para Python
def call(Map config = [:]) {
    def pythonPath = config.path ?: '.'
    def runSonar = config.runSonar == false
    def runDocker = config.runDocker == false
    def runDockerPush = config.runDockerPush == false
    def runCheckov = config.runCheckov == false
    def runPublishArtifact = config.runPublishArtifact == false
    
    pipeline {
        agent any
        // ETAPAS OBLIGATORIAS - Siempre se ejecutan
        stages{
            stage('Instalación de dependencias') {
                steps {
                    dir(pythonPath) {
                        sh '''
                            # Crear entorno virtual si no existe
                            if [ ! -d ".venv" ]; then
                                python3 -m venv .venv
                            fi
                            # Activar entorno virtual
                            . .venv/bin/activate
                            
                            ls

                            # Instalar dependencias
                            python -m pip install --upgrade pip
                            pip install -r requirements.txt
                        '''
                    }
                }
            }
            
            stage('Pruebas unitarias') {
                steps {
                    dir(pythonPath) {
                        sh '''
                            # Activar entorno virtual
                            . .venv/bin/activate
                            # Instalar pytest-junit si no está instalado
                            pip install pytest-junit || true
                            # Ejecutar pruebas con generación de reporte JUnit XML
                            python -m pytest --junitxml=test-results.xml
                        '''
                    }
                }
                post {
                    always {
                        dir(pythonPath) {
                            junit 'test-results.xml'
                        }
                    }
                }
            }
            
            // ETAPAS OPCIONALES - Se ejecutan según configuración
            stage('Análisis con SonarQube') {
                when {
                    expression { runSonar }
                }
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
                when {
                    expression { runSonar }
                }
                steps {
                    timeout(time: 5, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }
                }
            }

            stage('Publicación de artefactos') {
                when {
                    expression { runPublishArtifact }
                }
                steps {
                    dir(pythonPath) {
                        archiveArtifacts artifacts: 'dist/*.whl', fingerprint: true
                    }
                }
            }
            
            stage('Análisis con Checkov') {
                when {
                    expression { runCheckov }
                }
                steps {
                    dir(pythonPath) {
                        sh '''
                            # Activar entorno virtual
                            . .venv/bin/activate
                            # Instalar y ejecutar Checkov
                            pip install checkov
                            python -m checkov -d .
                        '''
                    }
                }
            }
            
            stage('Docker Build') {
                when {
                    expression { runDocker }
                }
                steps {
                    dir(pythonPath) {
                        script {
                            def imageName = "${env.JOB_NAME}:${env.BUILD_NUMBER}"
                            env.DOCKER_IMAGE_NAME = imageName
                            docker.build(imageName)
                        }
                    }
                }
            }
            
            stage('Docker Push') {
                when {
                    expression { runDocker && runDockerPush }
                }
                steps {
                    dir(pythonPath) {
                        withCredentials([usernamePassword(credentialsId: 'docker-registry', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                            script {
                                def imageName = env.DOCKER_IMAGE_NAME
                                def registryImage = "${DOCKER_USERNAME}/${imageName}"
                                sh "docker tag ${imageName} ${registryImage}"
                                docker.withRegistry('', 'docker-registry') {
                                    docker.image(registryImage).push()
                                }
                            }
                        }
                    }
                }
            }
            
        }
    }
}
