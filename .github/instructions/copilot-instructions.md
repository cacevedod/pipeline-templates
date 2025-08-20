# Copilot Instructions for pipeline-templates

## Overview

This repository centralizes reusable CI/CD pipeline templates for:

- **GitHub Actions** (Reusable Workflows in `.github/workflows/`)
- **Azure DevOps** (YAML templates in `azure-devops/`)
- **Jenkins** (Shared Library in `jenkins/`)

The goal is to allow projects to reference these templates directly, enabling global updates and consistent automation across multiple repositories.

## Key Structure

- `.github/workflows/`: Reusable GitHub Actions workflows (see `python-ci-template.yml`)
- `azure-devops/`: YAML templates for Azure DevOps pipelines (see `python-ci-template.yml`)
- `jenkins/vars/`: Groovy scripts for Jenkins Shared Library (see `pythonCi.groovy`)
- `examples/`: Example projects showing how to consume each template

## Usage Patterns

- **GitHub Actions**: Projects use `uses: <repo>/.github/workflows/<template>@<ref>` and pass parameters/secrets via `with` and `secrets`.
- **Azure DevOps**: Pipelines use `extends.template` referencing this repo as a resource, passing parameters as needed.
- **Jenkins**: Projects load the shared library and call the pipeline function with a config map.

## Developer Workflows

- Templates are versioned in this repo. Projects should reference a specific branch/tag for stability.
- To update logic for all consumers, update the template here and notify downstream projects to update their reference if not using `main`.
- Example usage for each system is in `examples/` with a README in each subfolder.

## Conventions & Patterns

- All templates are parameterized for language version, test toggles, and credentials (SonarQube, Docker, etc.).
- Secrets and credentials are always passed from the consuming project, not hardcoded.
- Each template includes: build, unit test, SonarQube (with Quality Gate), Checkov, artifact publish, Docker build/push.
- Jenkins shared library uses a single entrypoint per language (e.g., `pythonCi`).
- Keep templates minimal; project-specific logic should be handled in the consumer repo.

## Integration Points

- SonarQube, Docker Registry, and Checkov are integrated via environment variables or service connections.
- Example secrets/variables: `SONAR_TOKEN`, `DOCKER_REGISTRY`, `DOCKER_USERNAME`, `DOCKER_PASSWORD`.

## References

- See main `README.md` for high-level documentation.
- See `examples/` for concrete usage in each CI system.

---

If you add new templates or change integration patterns, update this file and the relevant example projects.
