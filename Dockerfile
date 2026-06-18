# Step 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN ./mvnw clean package -DskipTests

# Step 2: Run the application using a lightweight JDK image
FROM eclipse-temurin:21-jre-jammy
COPY --from=build /target/Healthcare-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]