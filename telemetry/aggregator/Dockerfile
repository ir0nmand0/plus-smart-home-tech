FROM amazoncorretto:21.0.6
WORKDIR /app
COPY target/*.jar aggregator.jar
HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost:9092/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "aggregator.jar"]