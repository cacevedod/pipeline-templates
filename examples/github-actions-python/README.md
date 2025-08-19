# Ejemplo: Uso de template centralizado de GitHub Actions para Python

Este ejemplo muestra cómo referenciar directamente el workflow reutilizable de CI para Python desde el repositorio central.

## Estructura

- `.github/workflows/ci.yml` (consume el template centralizado)
- Código fuente Python

## Uso recomendado

No copies el template, simplemente referencia el workflow centralizado en tu archivo `.github/workflows/ci.yml`:

```yaml
name: CI Python Example (Reusable)

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  use-template:
    uses: cacevedod/pipeline-templates/.github/workflows/python-ci-template.yml@main
    with:
      python-version: "3.11"
      run-tests: true
    secrets:
      SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      DOCKER_REGISTRY: ${{ secrets.DOCKER_REGISTRY }}
      DOCKER_USERNAME: ${{ secrets.DOCKER_USERNAME }}
      DOCKER_PASSWORD: ${{ secrets.DOCKER_PASSWORD }}
```

## Variables y secrets requeridos

| Nombre          | Descripción                        |
|-----------------|------------------------------------|
| SONAR_TOKEN     | Token de SonarQube (opcional)      |
| DOCKER_REGISTRY | URL del registry Docker (opcional) |
| DOCKER_USERNAME | Usuario Docker (opcional)          |
| DOCKER_PASSWORD | Password Docker (opcional)         |

## Notas

- Puedes cambiar el branch/tag en `@main` para fijar una versión estable.
- Consulta el archivo del template para más parámetros disponibles.
