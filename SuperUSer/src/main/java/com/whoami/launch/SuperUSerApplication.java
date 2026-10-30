package com.whoami.launch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SuperUSerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuperUSerApplication.class, args);
	}

}
