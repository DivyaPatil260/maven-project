# Step 1: Build stage
FROM maven:3.8.6-openjdk-8-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Step 2: Runtime stage
FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=build /app/target/prepaid-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
