FROM eclipse-temurin:21-jre

WORKDIR /app

ENV TZ=UTC

# Копируем уже собранный .jar (собран локально через ./mvnw package)
COPY target/mail-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]