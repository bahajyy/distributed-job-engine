FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY producer-api/pom.xml producer-api/pom.xml
COPY worker-service/pom.xml worker-service/pom.xml

RUN mvn dependency:go-offline -pl common,worker-service -am -q

COPY common/src common/src
COPY worker-service/src worker-service/src

RUN mvn clean package -pl common,worker-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/worker-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
