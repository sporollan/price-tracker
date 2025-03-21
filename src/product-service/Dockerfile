FROM eclipse-temurin:22-alpine

WORKDIR /code

# 1. Separate dependency resolution from source code changes
COPY mvnw* ./
COPY .mvn/ .mvn/
COPY pom.xml ./

RUN ./mvnw dependency:resolve

# 3. Copy source code after dependencies are resolved
COPY src/ src/

EXPOSE 8080

# 4. Build and run with clean dependencies
ENTRYPOINT ["./mvnw", "clean", "spring-boot:run"]