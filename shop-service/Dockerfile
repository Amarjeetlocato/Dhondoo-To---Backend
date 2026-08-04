# Build Stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn -B clean package -DskipTests -pl shop-service -am

# Runtime Stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/shop-service/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java","-jar","app.jar"]
