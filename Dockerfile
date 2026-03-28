# Build stage
FROM maven:3.9.11-eclipse-temurin-17 AS build
COPY . .
# Fix for the "Permission denied" error
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /target/Authifyy-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
