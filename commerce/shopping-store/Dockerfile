FROM amazoncorretto:21.0.6
WORKDIR /app
COPY target/*.jar shopping-store.jar
HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost:8181/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "shopping-store.jar"]