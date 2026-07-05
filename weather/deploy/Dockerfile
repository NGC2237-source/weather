FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="weather-system"

WORKDIR /app
COPY app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "app.jar"]