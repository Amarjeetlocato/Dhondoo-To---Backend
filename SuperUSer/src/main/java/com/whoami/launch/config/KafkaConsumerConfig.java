package com.whoami.launch.config;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;
    @Value("${spring.kafka.properties.security.protocol:}")
    private String securityProtocol;
    @Value("${spring.kafka.properties.sasl.mechanism:}")
    private String saslMechanism;
    @Value("${spring.kafka.properties.sasl.jaas.config:}")
    private String saslJaasConfig;
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "superuser-group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        if (hasSaslConfiguration()) {
            config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
            config.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
            config.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        }
        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(Object.class, false))
        );
    }
    private boolean hasSaslConfiguration() {
        return securityProtocol != null && !securityProtocol.isBlank()
                && saslMechanism != null && !saslMechanism.isBlank()
                && saslJaasConfig != null && !saslJaasConfig.isBlank();
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
