---
applyTo: "**"
---

# Instrucciones para la Creación de Proyectos

## Proyectos Python

### Estructura del Proyecto

Para crear un nuevo proyecto Python, sigue esta estructura estandarizada:

```
proyecto-python/
├── app/                      # Código fuente principal
│   ├── __init__.py           # Convierte app en un paquete Python
│   ├── main.py               # Punto de entrada principal
│   └── [módulos].py          # Otros módulos de la aplicación
├── tests/                    # Pruebas unitarias
│   ├── __init__.py           # Convierte tests en un paquete Python
│   ├── test_main.py          # Pruebas para main.py
│   └── test_[módulos].py     # Pruebas para otros módulos
├── .gitignore                # Archivos y directorios ignorados por git
├── Dockerfile                # Configuración para crear una imagen Docker
├── Jenkinsfile               # Configuración de CI/CD para Jenkins
├── README.md                 # Documentación del proyecto
├── requirements.txt          # Dependencias de producción y desarrollo
└── sonar-project.properties  # Configuración para análisis de código con SonarQube
```

Alternativamente, puedes usar `src/` en lugar de `app/` para el código fuente principal.

# Instrucciones para la Creación de Proyectos

Estas instrucciones permiten crear proyectos base en Python o Node.js, y configurar pipelines de CI/CD usando Jenkins o GitHub Actions, reutilizando los templates de este repositorio.

## Proyectos Python

### Estructura del Proyecto

Para crear un nuevo proyecto Python, sigue esta estructura estandarizada:

```
proyecto-python/
├── app/                      # Código fuente principal
│   ├── __init__.py           # Convierte app en un paquete Python
│   ├── main.py               # Punto de entrada principal
│   └── [módulos].py          # Otros módulos de la aplicación
├── tests/                    # Pruebas unitarias
│   ├── __init__.py           # Convierte tests en un paquete Python
│   ├── test_main.py          # Pruebas para main.py
│   └── test_[módulos].py     # Pruebas para otros módulos
├── .gitignore                # Archivos y directorios ignorados por git
├── Dockerfile                # Configuración para crear una imagen Docker
├── README.md                 # Documentación del proyecto
├── requirements.txt          # Dependencias de producción y desarrollo
└── [pipeline]                # Jenkinsfile o .github/workflows/ci.yml según CI/CD
```

Alternativamente, puedes usar `src/` en lugar de `app/` para el código fuente principal.

### Selección de CI/CD

Al crear un proyecto Python, puedes elegir el sistema de CI/CD:

- **Jenkins**: Copia el `Jenkinsfile` desde `jenkins-python` o el template correspondiente.
- **GitHub Actions**: Copia el workflow desde `github-actions/python/ci.yml` y colócalo en `.github/workflows/ci.yml`.

El agente debe crear la estructura base y copiar el pipeline adecuado según la opción indicada.

### Ejemplos de comandos para el agente

#### Python

> crear un proyecto nuevo de python usando github actions

> crear un proyecto nuevo de python usando jenkins

#### Node.js

> crear un proyecto nuevo de nodejs usando github actions

> crear un proyecto nuevo de nodejs usando jenkins

En cada caso, el agente debe:

1. Crear la estructura base del proyecto.
2. Copiar el pipeline adecuado desde los templates del repositorio:
   - Para GitHub Actions: copiar el workflow de `github-actions/[lenguaje]/ci.yml` a `.github/workflows/ci.yml`.
   - Para Jenkins: copiar el `Jenkinsfile` desde el template correspondiente.
3. Copiar los archivos base (`.gitignore`, `Dockerfile`, etc.) desde los templates.
4. Ajustar nombres y rutas según el nuevo proyecto.

---

## Proyectos Node.js (Fastify + TypeScript)

### Estructura del Proyecto

Para crear un nuevo proyecto Node.js con Fastify y TypeScript, sigue esta estructura:

```
proyecto-nodejs/
├── src/                  # Código fuente principal (TypeScript)
│   ├── app.ts            # Configuración de Fastify
│   └── routes/           # Rutas de la API
├── tests/                # Pruebas unitarias (Jest)
│   └── app.test.ts
├── .github/
│   └── workflows/
│       └── ci.yml        # Workflow de CI/CD (GitHub Actions)
├── .gitignore
├── Dockerfile
├── jest.config.js
├── package.json
├── tsconfig.json
└── README.md
```

### Pasos para crear un proyecto Node.js con CI/CD moderno

1. **Crea la estructura base** como se muestra arriba.
2. **Copia los archivos base** (`.gitignore`, `Dockerfile`, `jest.config.js`, `tsconfig.json`, etc.) desde los templates de este repositorio.
3. **Configura el pipeline de GitHub Actions**:

   - Crea `.github/workflows/ci.yml` y referencia el workflow reusable:

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

   - **No copies todo el pipeline**, solo referencia el template y ajusta los parámetros.

4. **Asegúrate de tener en tu `jest.config.js`** la configuración para generar el reporte JUnit:

   ```js
   reporters: [
     "default",
     [
       "jest-junit",
       {
         outputDirectory: process.env.JEST_JUNIT_OUTPUT_DIR || ".",
         outputName: process.env.JEST_JUNIT_OUTPUT_NAME || "jest-junit.xml",
       },
     ],
   ];
   ```

   El pipeline ya se encarga de pasar las variables de entorno para que el reporte se genere en la ruta correcta.

5. **Visualización de resultados en GitHub Actions**:

   - El pipeline publica los resultados de tests unitarios en la pestaña **Checks** usando dorny/test-reporter.
   - Si usas SonarQube, tendrás un enlace directo al dashboard en el summary del job.

6. **Dockerfile**: Usa `npm install` (no `--omit=dev`) para que el build funcione correctamente.

7. **.gitignore**: Incluye `.venv/`, `node_modules/`, `coverage/`, `jest-junit.xml`, y archivos temporales de IDE/SO.

### Buenas Prácticas

- Usa TypeScript estricto.
- Mantén el código y las pruebas separados.
- Usa Jest con cobertura y reporte JUnit.
- Documenta el proyecto en el README.
- Usa el workflow reusable para CI/CD.
- Usa Docker para builds y despliegue.
- Si usas SonarQube, configura el archivo `sonar-project.properties`.

### Contenido Inicial de los Archivos

#### 1. `src/app.ts`

```typescript
import Fastify from "fastify";
import rootRoute from "./routes/root";

const app = Fastify();
app.register(rootRoute);

const start = async () => {
  try {
    await app.listen({ port: 3000, host: "0.0.0.0" });
    console.log("Servidor iniciado en http://localhost:3000");
  } catch (err) {
    app.log.error(err);
    process.exit(1);
  }
};

if (require.main === module) {
  start();
}

export default app;
```

#### 2. `src/routes/root.ts`

```typescript
import { FastifyInstance, FastifyPluginOptions } from "fastify";

export default function rootRoute(
  app: FastifyInstance,
  opts: FastifyPluginOptions,
  done: () => void
) {
  app.get("/", async (request, reply) => {
    return { message: "¡Hola mundo desde Fastify + TypeScript!" };
  });
  done();
}
```

#### 3. `tests/app.test.ts`

```typescript
import app from "../src/app";
import supertest from "supertest";

describe("GET /", () => {
  it("debe responder con mensaje de bienvenida", async () => {
    const response = await supertest(app.server).get("/");
    expect(response.status).toBe(200);
    expect(response.body).toEqual({
      message: "¡Hola mundo desde Fastify + TypeScript!",
    });
  });
});
```

#### 4. `package.json`

```json
{
  "name": "proyecto-nodejs",
  "version": "1.0.0",
  "description": "Proyecto Node.js con Fastify, TypeScript y Jest",
  "main": "dist/app.js",
  "scripts": {
    "build": "tsc",
    "start": "node dist/app.js",
    "dev": "ts-node-dev --respawn --transpile-only src/app.ts",
    "test": "jest --coverage",
    "test:ci": "jest --ci --reporters=default --reporters=jest-junit"
  },
  "dependencies": {
    "fastify": "^4.27.2"
  },
  "devDependencies": {
    "@types/jest": "^29.5.11",
    "@types/node": "^20.11.30",
    "@types/fastify": "^4.15.2",
    "jest": "^29.7.0",
    "jest-junit": "^16.0.0",
    "ts-jest": "^29.1.2",
    "ts-node": "^10.9.2",
    "ts-node-dev": "^2.0.0",
    "typescript": "^5.4.5",
    "supertest": "^6.3.4"
  },
  "jest": {
    "preset": "ts-jest",
    "testEnvironment": "node",
    "collectCoverage": true,
    "coverageDirectory": "coverage",
    "reporters": [
      "default",
      ["jest-junit", { "outputDirectory": ".", "outputName": "jest-junit.xml" }]
    ]
  }
}
```

#### 5. `tsconfig.json`

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "module": "commonjs",
    "outDir": "dist",
    "rootDir": "src",
    "strict": true,
    "esModuleInterop": true,
    "forceConsistentCasingInFileNames": true,
    "skipLibCheck": true
  },
  "include": ["src"],
  "exclude": ["node_modules", "dist", "tests"]
}
```

#### 6. `.gitignore`

```ignore
# Node
node_modules/

# Python
__pycache__/
*.py[cod]
*$py.class
*.so
.Python
build/
develop-eggs/
dist/
downloads/
eggs/
.eggs/
lib/
lib64/
parts/
sdist/
var/
wheels/
*.egg-info/
.installed.cfg
*.egg

# Entornos virtuales
.env
.venv
env/
venv/
ENV/

# Cobertura y testing
.coverage
htmlcov/
.pytest_cache/
test-results.xml
coverage.xml

# IDE
.idea/
.vscode/
*.swp
*.swo

# Sistema operativo
.DS_Store
Thumbs.db
```

#### 7. `Dockerfile`

```dockerfile
FROM node:20-slim

WORKDIR /app

COPY package*.json ./
RUN npm install --omit=dev

COPY . .

RUN npm run build

USER node

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
        CMD node -e "require('http').get('http://localhost:3000', res => process.exit(res.statusCode === 200 ? 0 : 1)).on('error', () => process.exit(1))"

CMD ["node", "dist/app.js"]
```

#### 8. `.github/workflows/ci.yml`

**No crees un workflow completo en cada proyecto. Usa el template reusable y solo define los parámetros necesarios.**

Ejemplo de uso del workflow reusable:

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

##### Parámetros principales:

- `working-directory`: Carpeta donde está el código (por defecto `.`).
- `node-version`: Versión de Node.js a usar (por defecto `20`).
- `docker-image-name`: Nombre/tag de la imagen Docker a construir y publicar.

Puedes agregar más parámetros en el template reusable y documentarlos aquí.

---

**Importante:** Los agentes deben crear el workflow de cada proyecto usando la sintaxis `uses:` y pasar solo los parámetros requeridos, nunca copiar todo el pipeline. Así, cualquier mejora o fix en el template se replica automáticamente en todos los proyectos.

#### 9. `sonar-project.properties`

```properties
# Información básica del proyecto
sonar.projectKey=nombre-del-proyecto
sonar.projectName=Nombre del Proyecto
sonar.projectVersion=1.0

# Rutas donde SonarQube buscará código fuente
sonar.sources=app
sonar.exclusions=**/__pycache__/**,**/*.pyc,**/*.md

# Rutas para los tests y reportes
sonar.tests=tests
sonar.python.coverage.reportPaths=coverage.xml
sonar.python.xunit.reportPath=test-results.xml

# Codificación de los archivos fuente
sonar.sourceEncoding=UTF-8

# Parámetros adicionales
sonar.python.version=3
```

#### 10. `Jenkinsfile`

```groovy
@Library('pipeline-templates@main') _

pythonCi(
    path: '.',
    runSonar: true,
    sonarQubeInstallation: 'SonarQube',
    sonarScannerTool: 'SonarScanner',
    sonarProjectKey: 'nombre-del-proyecto',
    sonarProjectName: 'Nombre del Proyecto',
    runDocker: true
)
```

---

## Proyectos Java con Spring Boot y Gradle Wrapper

Para crear un nuevo proyecto Java con Spring Boot y Gradle Wrapper, sigue estos pasos:

### Estructura del Proyecto

Se recomienda la siguiente estructura:

```
proyecto-java/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ejemplo/
│   │   │           ├── DemoApplication.java
│   │   │           └── controller/
│   │   │                └── HelloController.java
│   │   └── resources/
│   │         └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── ejemplo/
│                   └── DemoApplicationTests.java
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle
├── settings.gradle
├── gradlew            # Script para Unix
├── gradlew.bat        # Script para Windows
├── .gitignore
├── Dockerfile         # Opcional: para contenedorización
├── README.md
└── [pipeline]         # Jenkinsfile o .github/workflows/ci.yml según tu CI/CD
```

### Configuración del Proyecto

1. En el archivo `build.gradle`:

   - Agrega las dependencias de Spring Boot y configura la tarea `bootRun` para ejecutar la aplicación.

2. En `settings.gradle` define el nombre del proyecto:

   ```gradle
   rootProject.name = 'proyecto-java'
   ```

3. Usa los scripts `gradlew` y `gradlew.bat` para garantizar que se use la misma versión de Gradle en todos los entornos.

4. Configura el pipeline de CI/CD:
   - **Jenkins**: Copia el template `Jenkinsfile` correspondiente desde `jenkins-java` o el template adecuado.
   - **GitHub Actions**: Copia el workflow desde `github-actions-java/ci.yml` a la ruta `.github/workflows/ci.yml`.

### Ejemplo de Dockerfile

Un ejemplo simple de `Dockerfile` es:

```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Comandos Útiles

- Compilar el proyecto: `./gradlew build`
- Ejecutar la aplicación: `./gradlew bootRun`
- Ejecutar pruebas: `./gradlew test`

### Buenas Prácticas

1. **Separación de código y pruebas**: Usa `src/` para código y `tests/` para unitarias.
2. **TypeScript estricto**: Mantén la configuración estricta en `tsconfig.json`.
3. **Cobertura y reporte JUnit**: Usa Jest con cobertura y reporte JUnit para CI.
4. **Contenedorización**: Usa Docker para estandarizar entornos.
5. **CI/CD**: Usa el workflow reusable de GitHub Actions.
6. **Documentación**: Mantén actualizado el README.md.

---

<!-- Puedes agregar más instrucciones para otros lenguajes y sistemas de CI/CD aquí -->

### Comandos Útiles

```bash
# Instalar dependencias
npm install

# Compilar TypeScript
npm run build

# Ejecutar pruebas unitarias
npm test

# Ejecutar en modo desarrollo
npm run dev
```

### Buenas Prácticas

1. **Separación de Responsabilidades**: Mantén el código fuente y las pruebas en directorios separados.
2. **Entorno Virtual**: Siempre usa un entorno virtual para aislar dependencias.
3. **Pruebas Unitarias**: Escribe pruebas para todo el código nuevo.
4. **Gestión de Dependencias**: Mantén `requirements.txt` actualizado.
5. **Documentación**: Proporciona un README.md claro y actualizado.
6. **CI/CD**: Utiliza el pipeline de Jenkins para integración continua.
7. **Análisis de Código**: Configura SonarQube para mejorar la calidad.
8. **Contenedorización**: Usa Docker para estandarizar entornos de ejecución.
