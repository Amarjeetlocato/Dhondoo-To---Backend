package com.whoami.launch.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

    @Bean
    ProducerFactory<String, Object> producerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                System.getenv().getOrDefault(
                        "KAFKA_SERVERS",
                        "kafka:9092"
                )
        );

        // ====================================
        // KAFKA SASL AUTHENTICATION
        // ====================================

        config.put(
                "security.protocol",
                System.getenv().getOrDefault(
                        "KAFKA_SECURITY_PROTOCOL",
                        "SASL_PLAINTEXT"
                )
        );

        config.put(
                SaslConfigs.SASL_MECHANISM,
                System.getenv().getOrDefault(
                        "KAFKA_SASL_MECHANISM",
                        "SCRAM-SHA-512"
                )
        );

        String username = System.getenv("KAFKA_USERNAME");
        String password = System.getenv("KAFKA_PASSWORD");

        config.put(
                SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.scram.ScramLoginModule required "
                        + "username=\"" + username + "\" "
                        + "password=\"" + password + "\";"
        );

        // ====================================
        // SERIALIZERS
        // ====================================

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}