FROM eclipse-temurin:22-alpine
WORKDIR /code
ADD ./ ./
RUN ["./mvnw", "package"]
EXPOSE 8080
ENTRYPOINT [ "java","-jar","./target/product-service-0.0.1-SNAPSHOT.jar" ]