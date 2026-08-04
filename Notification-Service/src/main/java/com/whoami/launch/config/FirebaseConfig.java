package com.whoami.launch.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * Firebase configuration for Cloud Messaging
 * Only initialized if firebase-config.json is available on classpath
 */
@Slf4j
@Configuration
@ConditionalOnResource(resources = "classpath:firebase-config.json")
public class FirebaseConfig {

    /**
     * Initialize Firebase App
     */
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            log.info("Initializing Firebase app");

            ClassPathResource resource = new ClassPathResource("firebase-config.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    /**
     * Firebase Messaging instance for sending messages
     */
    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) throws IOException {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
