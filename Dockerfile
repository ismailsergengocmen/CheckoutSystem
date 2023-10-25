# Stage 1: Build the application
FROM eclipse-temurin AS builder
WORKDIR /app
COPY . .
RUN ./mvnw package

# Stage 2: Create the final image
FROM eclipse-temurin
ARG JAR_FILE=/app/target/*.jar
COPY --from=builder ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]