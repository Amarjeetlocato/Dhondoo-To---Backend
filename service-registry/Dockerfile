# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src/

RUN mvn -B clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create a non-root user
RUN groupadd -r spring && useradd -r -g spring spring

COPY --from=builder /app/target/*.jar app.jar

# Make the application readable
RUN chown spring:spring /app/app.jar

USER 999:999

EXPOSE 8761

ENTRYPOINT ["java","-jar","app.jar"]