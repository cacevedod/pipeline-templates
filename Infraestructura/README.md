# Entorno de Pruebas Local: Jenkins + SonarQube

Este entorno permite levantar rápidamente Jenkins y SonarQube Community (con PostgreSQL) para probar los templates de CI/CD de este repositorio.

## Requisitos

- Docker y Docker Compose instalados

## Uso rápido

1. Desde la carpeta `Infraestructura`, ejecuta:

   ```sh
   docker-compose up -d
   ```

2. Accede a los servicios:

   - Jenkins: [http://localhost:8080](http://localhost:8080)
   - SonarQube: [http://localhost:9000](http://localhost:9000)

3. Primer uso:

   - Jenkins: El usuario y contraseña inicial están en los logs del contenedor (`docker logs jenkins`).
   - SonarQube: Usuario/contraseña por defecto: `admin` / `admin`.

4. Para detener y limpiar:

   ```sh
   docker-compose down -v
   ```

## Notas

- Los datos de Jenkins y SonarQube se almacenan en volúmenes Docker para persistencia.
- Puedes modificar el archivo `docker-compose.yml` para ajustar puertos o versiones.
