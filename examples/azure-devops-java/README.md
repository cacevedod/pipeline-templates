# Ejemplo Java + Spring Boot + Gradle Wrapper para Azure DevOps

Este proyecto es un ejemplo de aplicación Java usando Spring Boot y Gradle Wrapper, preparado para CI/CD en Azure DevOps utilizando un pipeline basado en template centralizado.

## ¿Cómo funciona el pipeline?

El archivo `azure-pipelines.yml` extiende el template `azure-devops/java-ci-template.yml` de este repositorio. Esto permite mantener la lógica de CI/CD reutilizable y actualizable desde un solo lugar.

### Parámetros del template

| Parámetro         | Descripción                                   | Valor por defecto |
|-------------------|-----------------------------------------------|-------------------|
| workingDirectory  | Carpeta raíz del proyecto                     | .                 |
| javaVersion       | Versión de Java a usar                        | 17                |
| runSonar          | Ejecuta análisis SonarQube                    | false             |
| runCheckov        | Ejecuta análisis de seguridad Checkov         | false             |
| runDockerBuild    | Construye la imagen Docker                    | false             |
| runDockerPush     | Publica la imagen Docker                      | false             |

#### Ejemplo de uso en `azure-pipelines.yml`

```yaml
trigger:
  - main

extends:
  template: ../../azure-devops/java-ci-template.yml
  parameters:
    workingDirectory: .
    javaVersion: '17'
    runSonar: true
    runCheckov: true
    runDockerBuild: true
    runDockerPush: false
```

## Requisitos previos para ejecutar el pipeline

1. **Crear el Service Connection de SonarQube y el template (GitHub)**
   - Ve a la sección de Service Connections en tu proyecto de Azure DevOps.
   - Crea una conexión de tipo SonarQube llamada `SonarQubeServer`.
   - Crea una conexión de tipo Docker Registry si vas a publicar imágenes.
   - ![Service Connection SonarQube](img/sonar-server.png)
   - ![Service Connection Template](img/sonar-scanner.png)

2. **Crear el Service Connection de DockerHub (Registry)**
   - Ve a la sección de Service Connections y crea una conexión de tipo Docker Registry.
   - Selecciona DockerHub como proveedor y configura las credenciales de tu cuenta.
   - ![Service Connection DockerHub](img/dockerhub-connection.png)

3. **Tener instalado SonarQube en el proyecto de Azure DevOps**
   - Instala la extensión de SonarQube desde el marketplace de Azure DevOps.
   - ![Instalar extensión SonarQube](img/sonar-webhook.png)

4. **(Opcional) Azure Resource Manager**
   - Para despliegues automáticos a Azure.

## Jobs principales del pipeline

- **Build & Unit Tests**: Compila y ejecuta pruebas con Gradle, publica resultados y cobertura.
- **SonarQube Analysis**: (opcional) Ejecuta análisis de calidad si `runSonar` es true.
- **Security & Docker**: (opcional) Ejecuta Checkov y construye/publica imagen Docker según los flags.

## Ejecución local

Compila y prueba localmente con:

```sh
./gradlew build
./gradlew test
```

Construye la imagen Docker:

```sh
docker build -t azure-devops-java:latest .
```

## Notas

- El pipeline centralizado facilita la actualización y mantenimiento de la lógica de CI/CD.
- El Dockerfile sigue buenas prácticas de seguridad (usuario no root, healthcheck).
- El archivo `.gitignore` debe incluir carpetas y archivos generados por Gradle, Java y Docker.

---

¿Dudas o sugerencias? ¡Contribuye o abre un issue!
