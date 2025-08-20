// Define las etapas del pipeline para Python
def call(Map config = [:]) {
    def pythonPath = config.path ?: '.'
    def runSonar = config.runSonar ?: false
    def runDocker = config.runDocker ?: false
    def runDockerPush = config.runDockerPush ?: false
    def runCheckov = config.runCheckov ?: false
    def runPublishArtifact = config.runPublishArtifact ?: false
    def sonarQubeInstallation = config.sonarQubeInstallation ?: 'SonarQube'
    def sonarScannerTool = config.sonarScannerTool ?: 'SonarScanner'
    def sonarProjectKey = config.sonarProjectKey ?: "${env.JOB_NAME.replace('/', '_')}"
    def sonarProjectName = config.sonarProjectName ?: "${env.JOB_NAME}"
    
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
                            . .venv/bin/activate
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
                            . .venv/bin/activate
                            python -m pytest tests/ --junitxml=test-results.xml --cov=app --cov-report=xml
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
                            withSonarQubeEnv(sonarQubeInstallation) {
                                // Usar la herramienta SonarScanner configurada en Jenkins
                                script {
                                    def scannerHome = tool sonarScannerTool
                                    // Verificar si existe sonar-project.properties o crearlo
                                    sh """
                                        if [ ! -f "sonar-project.properties" ]; then
                                            echo "# Creando configuración predeterminada para SonarQube" > sonar-project.properties
                                            echo "sonar.projectKey=${sonarProjectKey}" >> sonar-project.properties
                                            echo "sonar.projectName=${sonarProjectName}" >> sonar-project.properties
                                            echo "sonar.projectVersion=${env.BUILD_NUMBER}" >> sonar-project.properties
                                            
                                            # Detectar estructura del proyecto
                                            if [ -d "app" ] && [ -d "tests" ]; then
                                                echo "# Estructura recomendada detectada: app/ para código fuente y tests/ para pruebas" >> sonar-project.properties
                                                echo "sonar.sources=app" >> sonar-project.properties
                                                echo "sonar.tests=tests" >> sonar-project.properties
                                            elif [ -d "src" ] && [ -d "tests" ]; then
                                                echo "# Estructura recomendada detectada: src/ para código fuente y tests/ para pruebas" >> sonar-project.properties
                                                echo "sonar.sources=src" >> sonar-project.properties
                                                echo "sonar.tests=tests" >> sonar-project.properties
                                            elif [ -d "app" ] && [ ! -d "tests" ]; then
                                                echo "# ADVERTENCIA: Se encontró la carpeta app/ pero no la carpeta tests/" >> sonar-project.properties
                                                echo "# Se recomienda seguir la estructura: app/ para código y tests/ para pruebas" >> sonar-project.properties
                                                echo "sonar.sources=app" >> sonar-project.properties
                                                echo "sonar.exclusions=**/__pycache__/**,**/*.pyc,**/*.md,**/*test_*.py" >> sonar-project.properties
                                                echo "sonar.tests=app" >> sonar-project.properties
                                                echo "sonar.test.inclusions=**/*test_*.py" >> sonar-project.properties
                                            else
                                                echo "# ADVERTENCIA: No se detectó una estructura de proyecto recomendada" >> sonar-project.properties
                                                echo "# Se recomienda seguir la estructura: app/ o src/ para código y tests/ para pruebas" >> sonar-project.properties
                                                echo "sonar.sources=." >> sonar-project.properties
                                                echo "sonar.exclusions=**/__pycache__/**,**/*.pyc,**/*.md,**/tests/**,**/*test_*.py" >> sonar-project.properties
                                                echo "sonar.tests=." >> sonar-project.properties
                                                echo "sonar.test.inclusions=**/*test_*.py" >> sonar-project.properties
                                            fi
                                            
                                            echo "sonar.exclusions+=**/__pycache__/**,**/*.pyc,**/*.md" >> sonar-project.properties
                                            echo "sonar.python.coverage.reportPaths=coverage.xml" >> sonar-project.properties
                                            echo "sonar.python.xunit.reportPath=test-results.xml" >> sonar-project.properties
                                            echo "sonar.sourceEncoding=UTF-8" >> sonar-project.properties
                                            echo "sonar.python.version=3" >> sonar-project.properties
                                            echo "# Configuración generada automáticamente - edite según sea necesario" >> sonar-project.properties
                                        else
                                            echo "Usando archivo sonar-project.properties existente"
                                        fi
                                    """
                                    sh "${scannerHome}/bin/sonar-scanner"
                                }
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
                            . .venv/bin/activate
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
