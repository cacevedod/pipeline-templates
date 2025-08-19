# DevCo Pipeline Templates

Este repositorio centraliza plantillas reutilizables de CI/CD para:

- **GitHub Actions** (workflows reutilizables en `.github/workflows/`)
- **Azure DevOps** (plantillas YAML en `azure-devops/`)
- **Jenkins** (librería compartida en `jenkins/`)

El objetivo es permitir que los proyectos referencien estas plantillas directamente, habilitando actualizaciones globales y automatización consistente en múltiples repositorios.

## Estructura

- `.github/workflows/`: Workflows reutilizables de GitHub Actions (ver `python-ci-template.yml`)
- `azure-devops/`: Plantillas YAML para pipelines de Azure DevOps (ver `python-ci-template.yml`)
- `jenkins/vars/`: Scripts Groovy para la Shared Library de Jenkins (ver `pythonCi.groovy`)
- `examples/`: Proyectos de ejemplo mostrando cómo consumir cada plantilla

## Patrones de Uso

- **GitHub Actions**: Los proyectos usan `uses: <repo>/.github/workflows/<template>@<ref>` y pasan parámetros/secrets vía `with` y `secrets`.
- **Azure DevOps**: Los pipelines usan `extends.template` referenciando este repo como recurso, pasando parámetros según sea necesario.
- **Jenkins**: Los proyectos cargan la shared library y llaman la función del pipeline con un config map.

## Flujos de Trabajo para Desarrolladores

- Las plantillas están versionadas en este repo. Los proyectos deben referenciar un branch/tag específico para estabilidad.
- Para actualizar la lógica para todos los consumidores, actualiza la plantilla aquí y notifica a los proyectos downstream si no usan `main`.
- Ejemplo de uso para cada sistema en `examples/` con un README en cada subcarpeta.

## Convenciones y Patrones

- Todas las plantillas son parametrizables: versión de lenguaje, toggles de tests, credenciales (SonarQube, Docker, etc.).
- Los secretos y credenciales siempre se pasan desde el proyecto consumidor, nunca se hardcodean.
- Cada plantilla incluye: build, unit test, SonarQube (con Quality Gate), Checkov, publicación de artefactos, build/push Docker.
- La shared library de Jenkins usa un solo entrypoint por lenguaje (ej: `pythonCi`).
- Mantén las plantillas mínimas; la lógica específica de cada proyecto debe estar en el repo consumidor.

## Integraciones

- SonarQube, Docker Registry y Checkov se integran vía variables de entorno o service connections.
- Ejemplo de secrets/variables: `SONAR_TOKEN`, `DOCKER_REGISTRY`, `DOCKER_USERNAME`, `DOCKER_PASSWORD`.

## Referencias y Ejemplos

Para guías de uso detalladas y ejemplos prácticos, consulta el `README.md` correspondiente dentro de cada subcarpeta en `examples/`:

- `examples/azure-devops-python/README.md`
- `examples/github-actions-python/README.md`
- `examples/jenkins-python/README.md`

---
Si agregas nuevas plantillas o cambias patrones de integración, actualiza este archivo y los proyectos de ejemplo relevantes.
