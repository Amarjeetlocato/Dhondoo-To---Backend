
package com.whoami.launch.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.locato.constants.events.customer.CustomerProfileCreatedEvent;
import com.locato.constants.events.location.LocationCreatedEvent;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private static final String GROUP_ID =
            "shop-business-operation-group";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private final Environment environment;

    public KafkaConsumerConfig(Environment environment) {
        this.environment = environment;
    }

    private Map<String, Object> commonConsumerProperties() {

        Map<String, Object> props = new HashMap<>();

        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
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

        String protocol = environment.getProperty(
                "spring.kafka.properties.security.protocol"
        );

        String mechanism = environment.getProperty(
                "spring.kafka.properties.sasl.mechanism"
        );

        String jaasConfig = environment.getProperty(
                "spring.kafka.properties.sasl.jaas.config"
        );

        if (protocol != null) {
            props.put("security.protocol", protocol);
        }

        if (mechanism != null) {
            props.put("sasl.mechanism", mechanism);
        }

        if (jaasConfig != null) {
            props.put("sasl.jaas.config", jaasConfig);
        }

        return props;
    }

    // CUSTOMER PROFILE EVENT CONSUMER

    @Bean
    public ConsumerFactory<String, CustomerProfileCreatedEvent>
    customerConsumerFactory() {

        JsonDeserializer<CustomerProfileCreatedEvent> deserializer =
                new JsonDeserializer<>(
                        CustomerProfileCreatedEvent.class
                );

        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                commonConsumerProperties(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<
            String,
            CustomerProfileCreatedEvent>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<
                String,
                CustomerProfileCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(customerConsumerFactory());

        return factory;
    }

    // LOCATION EVENT CONSUMER

    @Bean
    public ConsumerFactory<String, LocationCreatedEvent>
    locationConsumerFactory() {

        JsonDeserializer<LocationCreatedEvent> deserializer =
                new JsonDeserializer<>(
                        LocationCreatedEvent.class
                );

        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                commonConsumerProperties(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "locationKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<
            String,
            LocationCreatedEvent>
    locationKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<
                String,
                LocationCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(locationConsumerFactory());

        return factory;
    }
}
