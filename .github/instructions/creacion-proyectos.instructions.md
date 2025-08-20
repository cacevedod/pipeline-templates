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

### Contenido Inicial de los Archivos

#### 1. `app/__init__.py`

```python
# Este archivo marca la carpeta app como un paquete Python
```

#### 2. `app/main.py` (ejemplo para FastAPI)

```python
from fastapi import FastAPI

app = FastAPI()

@app.get("/")
def read_root():
    return {"message": "Hola mundo desde FastAPI!"}
```

#### 3. `tests/__init__.py`

```python
# Este archivo marca la carpeta tests como un paquete Python
```

#### 4. `tests/test_main.py` (para FastAPI)

```python
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_read_root():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"message": "Hola mundo desde FastAPI!"}
```

#### 5. `requirements.txt`

```
fastapi>=0.103.1
uvicorn>=0.23.2
pytest>=7.4.0
pytest-cov>=4.1.0
httpx>=0.24.1  # Para TestClient en FastAPI
```

#### 6. `Dockerfile`

```dockerfile
FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
```

#### 7. `sonar-project.properties`

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

#### 8. `Jenkinsfile`

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

#### 9. `.gitignore`

```
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

### Comandos Útiles

#### Configuración Inicial

```bash
# Crear estructura de directorios
mkdir -p app tests

# Crear archivos __init__.py
touch app/__init__.py tests/__init__.py

# Instalar dependencias
python -m venv .venv
source .venv/bin/activate  # En Windows: .venv\Scripts\activate
pip install -r requirements.txt
```

#### Ejecutar la Aplicación (FastAPI)

```bash
uvicorn app.main:app --reload
```

#### Ejecutar Pruebas

```bash
pytest tests/ --cov=app --cov-report=xml --junitxml=test-results.xml
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

<!-- Las instrucciones para proyectos Node.js y Java se añadirán más adelante -->
