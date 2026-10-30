package com.whoami;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class BusinessOperationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusinessOperationServiceApplication.class, args);
	}

}
