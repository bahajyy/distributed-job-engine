FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only pom files first — Docker caches this layer until a pom changes
COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY producer-api/pom.xml producer-api/pom.xml
COPY worker-service/pom.xml worker-service/pom.xml

# Download dependencies before copying source — faster rebuilds on source-only changes
RUN mvn dependency:go-offline -pl common,producer-api -am -q

COPY common/src common/src
COPY producer-api/src producer-api/src

RUN mvn clean package -pl common,producer-api -am -DskipTests -q

# Runtime stage — only JRE, no Maven or source code
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/producer-api/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
