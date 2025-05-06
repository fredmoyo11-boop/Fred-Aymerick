FROM gradle:latest AS build
WORKDIR /app

COPY build.gradle .
COPY src ./src

RUN gradle clean build

FROM openjdk:21-jdk-slim
WORKDIR /app

ARG JAR_FILE=/app/build/libs/*.jar
COPY --from=build ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]