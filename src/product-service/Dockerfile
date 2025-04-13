# Stage 1: Build Stage
FROM eclipse-temurin:22-alpine AS build

# Set working directory for the build stage.
WORKDIR /app

# Copy the project files into the container.
COPY . .

# Ensure the Maven wrapper is executable, then package the application.
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests


# Stage 2: Run Stage
FROM eclipse-temurin:22-alpine

# Create a non-root user for additional security.
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set the working directory in the runtime container.
WORKDIR /app

# Copy the generated JAR from the build stage.
COPY --from=build /app/target/*.jar application.jar

# Expose the necessary port.
EXPOSE 8080

# Switch to non-root user for running the application.
USER appuser

# Entry point to run the Spring Boot application.
ENTRYPOINT ["java", "-jar", "application.jar"]
