/*
 * package com.whoami.launch.kafka;
 * 
 * import java.time.LocalDateTime; import java.util.UUID;
 * 
 * import org.springframework.kafka.core.KafkaTemplate; import
 * org.springframework.stereotype.Component;
 * 
 * import com.locato.constants.events.EventSources; import
 * com.locato.constants.events.EventVersions; import
 * com.locato.constants.events.product.ProductCreatedEvent; import
 * com.locato.constants.events.product.ProductDeletedEvent; import
 * com.locato.constants.events.product.ProductEventType; import
 * com.locato.constants.events.product.ProductUpdatedEvent; import
 * com.locato.constants.topics.KafkaTopics;
 * 
 * import lombok.RequiredArgsConstructor;
 * 
 * @Component
 * 
 * @RequiredArgsConstructor public class ProductEventProducer {
 * 
 * private final KafkaTemplate<String, Object> kafkaTemplate;
 * 
 * public void publishProductCreated(ProductCreatedEvent event) { prepareEvent(
 * event.getEventId(), event.getEventType() );
 * 
 * if (event.getEventId() == null) { event.setEventId(UUID.randomUUID()); }
 * 
 * event.setEventType(ProductEventType.PRODUCT_CREATED);
 * event.setEventVersion(EventVersions.V1);
 * event.setSource(EventSources.BUSINESS_SERVICE);
 * 
 * if (event.getOccurredAt() == null) {
 * event.setOccurredAt(LocalDateTime.now()); }
 * 
 * kafkaTemplate.send( KafkaTopics.PRODUCT_EVENTS, event.getProductId(), event
 * ); }
 * 
 * public void publishProductUpdated(ProductUpdatedEvent event) { if
 * (event.getEventId() == null) { event.setEventId(UUID.randomUUID()); }
 * 
 * event.setEventType(ProductEventType.PRODUCT_UPDATED);
 * event.setEventVersion(EventVersions.V1);
 * event.setSource(EventSources.BUSINESS_SERVICE);
 * 
 * if (event.getOccurredAt() == null) {
 * event.setOccurredAt(LocalDateTime.now()); }
 * 
 * kafkaTemplate.send( KafkaTopics.PRODUCT_EVENTS, event.getProductId(), event
 * ); }
 * 
 * public void publishProductDeleted(ProductDeletedEvent event) { if
 * (event.getEventId() == null) { event.setEventId(UUID.randomUUID()); }
 * 
 * event.setEventType(ProductEventType.PRODUCT_DELETED);
 * event.setEventVersion(EventVersions.V1);
 * event.setSource(EventSources.BUSINESS_SERVICE);
 * 
 * if (event.getOccurredAt() == null) {
 * event.setOccurredAt(LocalDateTime.now()); }
 * 
 * kafkaTemplate.send( KafkaTopics.PRODUCT_EVENTS, event.getProductId(), event
 * ); }
 * 
 * private void prepareEvent(UUID eventId, ProductEventType eventType) { //
 * Reserved for shared event preparation if needed. } }
 */