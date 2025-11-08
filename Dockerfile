FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar clinic.jar
COPY src/main/resources/static/img/default-doc.png /app/images/default-doc.png
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app/clinic.jar"]