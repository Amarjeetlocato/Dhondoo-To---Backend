# Build Stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn -B clean package -DskipTests -pl BusinessOperationService -am


# Runtime Stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/BusinessOperationService/target/BusinessOperationService-1.0.0.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]