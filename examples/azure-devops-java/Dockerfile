FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY build/libs/*.jar app.jar
# Crear usuario no root
RUN useradd -m appuser && chown appuser:appuser /app.jar
USER appuser
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD java -jar /app.jar --spring.profiles.active=healthcheck || exit 1
ENTRYPOINT ["java", "-jar", "/app.jar"]
