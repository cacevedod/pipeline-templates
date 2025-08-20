# Ejemplo de Node.js con Fastify, TypeScript y GitHub Actions

Este proyecto es un ejemplo de buenas prácticas para aplicaciones Node.js usando Fastify, TypeScript y pruebas unitarias con Jest. Incluye un workflow reusable para CI/CD en GitHub Actions.

## Estructura recomendada

```
examples/github-actions-nodejs/
├── src/                  # Código fuente principal (TypeScript)
│   ├── app.ts            # Configuración de Fastify
│   └── routes/           # Rutas de la API
├── tests/                # Pruebas unitarias (Jest)
│   └── app.test.ts
├── .github/
│   └── workflows/
│       └── ci.yml        # Workflow de CI/CD
├── .gitignore
├── Dockerfile
├── jest.config.js
├── package.json
├── tsconfig.json
└── README.md
```

## Comandos útiles

- `npm install` — Instala dependencias
- `npm run build` — Compila TypeScript
- `npm test` — Ejecuta pruebas unitarias
- `npm start` — Inicia la app en modo producción
- `npm run dev` — Inicia la app en modo desarrollo (hot reload)


## CI/CD con Workflow Reusable

Este proyecto utiliza un workflow reusable centralizado para CI/CD en GitHub Actions. Solo necesitas definir los parámetros requeridos en `.github/workflows/ci.yml` y el pipeline se mantendrá actualizado automáticamente con las mejoras del template.

### Ejemplo de configuración (`.github/workflows/ci.yml`):

```yaml
name: Node.js CI

on:
	push:
		branches: [main]
	pull_request:
		branches: [main]

jobs:
	build:
		uses: <usuario>/<repo-templates>/github-actions/nodejs/ci.yml@main
		with:
			working-directory: .
			node-version: 20
			docker-image-name: ghcr.io/${{ github.repository }}:${{ github.sha }}
		secrets: inherit
```

### Parámetros principales:

- `working-directory`: Carpeta donde está el código (por defecto `.`).
- `node-version`: Versión de Node.js a usar (por defecto `20`).
- `docker-image-name`: Nombre/tag de la imagen Docker a construir y publicar.

> **Importante:** No copies todo el pipeline, solo referencia el template y ajusta los parámetros. Así, cualquier mejora en el template se replica automáticamente en tu proyecto.

## Requisitos


> Sigue las mejores prácticas de Node.js y adapta este template según tus necesidades.

## Visualización de resultados en GitHub Actions

### Reporte visual de pruebas unitarias

El pipeline utiliza la acción [dorny/test-reporter](https://github.com/dorny/test-reporter) para mostrar los resultados de los tests unitarios en la pestaña **Checks** de cada pull request o commit. Esto permite ver fácilmente qué pruebas pasaron o fallaron, sin revisar los logs.

- El reporte se genera automáticamente a partir del archivo `jest-junit.xml`.
- Si hay fallos, se muestran en la interfaz de GitHub Actions.

### Enlace directo al informe de SonarQube

Si la opción de análisis SonarQube está habilitada, el pipeline publica un enlace directo al dashboard del proyecto en la pestaña **Summary** del job correspondiente. Así puedes acceder rápidamente al informe de calidad y cobertura de código.

- El enlace aparece como: `🔗 Ver informe SonarQube` en el resumen del job.
- Asegúrate de tener configurada la variable `SONAR_HOST_URL` en los secretos del repositorio.
