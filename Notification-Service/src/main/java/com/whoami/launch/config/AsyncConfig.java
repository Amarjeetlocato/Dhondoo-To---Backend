package com.whoami.launch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration for background tasks and FCM operations
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Defines a dedicated thread pool for asynchronous FCM operations.
     * This prevents I/O-bound tasks from blocking the main application threads.
     */
    @Bean(name = "fcmExecutor")
    public Executor fcmExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("FCM-Executor-");
        executor.initialize();
        return executor;
    }

    /**
     * Thread pool for general async tasks
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Task-Executor-");
        executor.initialize();
        return executor;
    }
}
    

