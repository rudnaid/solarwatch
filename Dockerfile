FROM eclipse-temurin:23-jre-alpine
WORKDIR /tmp
COPY target/*.jar solar-watch-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","solar-watch-0.0.1-SNAPSHOT.jar"]
