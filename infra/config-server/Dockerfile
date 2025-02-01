FROM amazoncorretto:21.0.6
WORKDIR /app
COPY target/*.jar config-server.jar
COPY src/main/resources/config /app/config
EXPOSE 8888
HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost:8888/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "config-server.jar"]