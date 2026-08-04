package com.whoami.launch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {

        return template -> {

            template.header(
                    "X-API-KEY",
                    "WHOAMI_INTERNAL_2026"
            );
        };
    }
} 