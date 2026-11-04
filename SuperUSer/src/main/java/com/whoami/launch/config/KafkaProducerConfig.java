package com.whoami.launch.config;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;
    @Value("${spring.kafka.properties.security.protocol:}")
    private String securityProtocol;
    @Value("${spring.kafka.properties.sasl.mechanism:}")
    private String saslMechanism;
    @Value("${spring.kafka.properties.sasl.jaas.config:}")
    private String saslJaasConfig;
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        if (hasSaslConfiguration()) {
            config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
            config.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
            config.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        }
        return new DefaultKafkaProducerFactory<>(config);
    }
    private boolean hasSaslConfiguration() {
        return securityProtocol != null && !securityProtocol.isBlank()
                && saslMechanism != null && !saslMechanism.isBlank()
                && saslJaasConfig != null && !saslJaasConfig.isBlank();
    }
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
