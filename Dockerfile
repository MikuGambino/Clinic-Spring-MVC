FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn package

FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/*.jar clinic.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app/clinic.jar"]