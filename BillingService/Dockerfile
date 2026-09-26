# Build Stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn -B clean package -DskipTests -pl BillingService -am
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/BillingService-1.0.0.jar app.jar

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "app.jar"]
