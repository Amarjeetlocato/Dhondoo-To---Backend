package com.whoami.launch.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        try {
            InputStream is = null;
            ClassPathResource resource = new ClassPathResource("firebase-config.json");
            if (resource.exists()) {
                is = resource.getInputStream();
            } else {
                java.io.File externalFile = new java.io.File("/app/firebase-config.json");
                if (externalFile.exists()) {
                    is = new java.io.FileInputStream(externalFile);
                }
            }

            if (is != null) {
                try (InputStream stream = is) {
                    GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(credentials)
                            .build();

                    log.info("Firebase App initialized successfully");
                    return FirebaseApp.initializeApp(options);
                }
            }
        } catch (Exception e) {
            log.warn("Firebase App initialization failed/skipped: {}", e.getMessage());
        }
        return null;
    }

    @Bean
    @ConditionalOnBean(FirebaseApp.class)
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        if (firebaseApp == null) {
            return null;
        }
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
