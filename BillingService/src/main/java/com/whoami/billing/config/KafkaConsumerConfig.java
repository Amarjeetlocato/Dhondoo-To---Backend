
package com.whoami.billing.config;

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
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.locato.constants.events.billing.BillingRequestedEvent;
import com.locato.constants.events.billing.RecurringPaymentRequestedEvent;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.properties.security.protocol}")
    private String securityProtocol;

    @Value("${spring.kafka.properties.sasl.mechanism}")
    private String saslMechanism;

    @Value("${spring.kafka.properties.sasl.jaas.config}")
    private String saslJaasConfig;

    private Map<String, Object> consumerConfig(String consumerGroup) {

        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // Kafka SASL/SCRAM authentication
        config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        config.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        config.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);

        return config;
    }

    @Bean
    public ConsumerFactory<String, BillingRequestedEvent> billingRequestedConsumerFactory() {

        JsonDeserializer<BillingRequestedEvent> deserializer =
                new JsonDeserializer<>(BillingRequestedEvent.class, false);

        deserializer.addTrustedPackages("com.locato.constants.events.billing");

        return new DefaultKafkaConsumerFactory<>(
                consumerConfig(groupId),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BillingRequestedEvent>
    billingRequestedKafkaListenerContainerFactory(
            ConsumerFactory<String, BillingRequestedEvent> billingRequestedConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, BillingRequestedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(billingRequestedConsumerFactory);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, RecurringPaymentRequestedEvent> recurringPaymentConsumerFactory() {

        JsonDeserializer<RecurringPaymentRequestedEvent> deserializer =
                new JsonDeserializer<>(RecurringPaymentRequestedEvent.class, false);

        deserializer.addTrustedPackages("com.locato.constants.events.billing");

        return new DefaultKafkaConsumerFactory<>(
                consumerConfig(groupId + "-recurring-payment"),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RecurringPaymentRequestedEvent>
    recurringPaymentKafkaListenerContainerFactory(
            ConsumerFactory<String, RecurringPaymentRequestedEvent> recurringPaymentConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, RecurringPaymentRequestedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(recurringPaymentConsumerFactory);

        return factory;
    }
}
