package com.whoami.launch.config;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
@Configuration
public class BusinessOperationKafkaConfig {
    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String GROUP_ID = "shop-business-operation-group";
    @Bean
    public ConsumerFactory<String, Object>
    businessOperationConsumerFactory() {
        JsonDeserializer<Object> deserializer =
                new JsonDeserializer<>(Object.class);
        deserializer.addTrustedPackages(
                "com.locato.constants.events.businessoperation"
        );
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS
        );
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                GROUP_ID
        );
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }
    @Bean(name = "businessOperationKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object>
    businessOperationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(
                businessOperationConsumerFactory()
        );
        return factory;
    }
}
