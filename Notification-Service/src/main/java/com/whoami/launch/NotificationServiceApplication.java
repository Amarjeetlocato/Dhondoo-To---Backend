package com.whoami.launch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

/**
 * Main Application Class for Notification Service
 * 
 * Features:
 * - Production-ready REST APIs for notification management
 * - Firebase Cloud Messaging (FCM) integration for push notifications
 * - MySQL database with optimized indexes
 * - Eureka service discovery integration
 * - OpenFeign for inter-service communication
 * - Swagger/OpenAPI documentation
 * - Actuator health endpoints
 * - Global exception handling
 * - Async task execution with thread pools
 * - Notification preferences and activity logging
 */

@OpenAPIDefinition
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
