FROM amazoncorretto:21.0.6
WORKDIR /app
COPY target/*.jar discovery-server.jar
EXPOSE 8761
HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost:8761/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "discovery-server.jar"]