FROM eclipse-temurin:23-jdk-alpine AS build
WORKDIR /app

COPY . .

RUN ./mvnw clean package

FROM eclipse-temurin:23-jre-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar solarwatch_backend.jar

ENTRYPOINT ["java", "-jar", "solarwatch_backend.jar"]
