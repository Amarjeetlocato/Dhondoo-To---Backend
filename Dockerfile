FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

RUN mvn -B clean package -DskipTests -q

# Eureka Service
FROM eclipse-temurin:21-jre AS eureka

WORKDIR /app

COPY --from=builder /app/service-registry/target/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java","-jar","app.jar"]

# User Registry Service
FROM eclipse-temurin:21-jre AS user-registry

WORKDIR /app

COPY --from=builder /app/userRegistry/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

# Shop Service
FROM eclipse-temurin:21-jre AS shop-service

WORKDIR /app

COPY --from=builder /app/shop-service/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java","-jar","app.jar"]

# Service Chat
FROM eclipse-temurin:21-jre AS service-chat

WORKDIR /app

COPY --from=builder /app/Servicechat/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","app.jar"]

# Order Service
FROM eclipse-temurin:21-jre AS order-service

WORKDIR /app

COPY --from=builder /app/OrderService/target/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java","-jar","app.jar"]

# Notification Service
FROM eclipse-temurin:21-jre AS notification-service

WORKDIR /app

COPY --from=builder /app/Notification-Service/target/*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java","-jar","app.jar"]

# Super User Service
# FROM eclipse-temurin:21-jre AS super-user

# WORKDIR /app

# COPY --from=builder /app/SuperUSer/target/*.jar app.jar

# EXPOSE 8088

# ENTRYPOINT ["java","-jar","app.jar"]

# Gateway Service
FROM eclipse-temurin:21-jre AS gateway

WORKDIR /app

COPY --from=builder /app/Gateway/target/*.jar app.jar

EXPOSE 8079

ENTRYPOINT ["java","-jar","app.jar"]
