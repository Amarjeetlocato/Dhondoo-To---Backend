package com.whoami.launch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Scheduling Configuration
 * Enables automatic scheduled tasks for security cleanup
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Scheduling is enabled here
    // SecurityService will use @Scheduled annotation for automatic cleanup
}
