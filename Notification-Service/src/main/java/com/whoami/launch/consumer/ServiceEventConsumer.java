package com.whoami.launch.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.whoami.launch.dto.KafkaTopics;
import com.locato.dto.ServiceCreatedEvent;
import com.locato.dto.ServiceDeletedEvent;
import com.locato.dto.ServiceUpdatedEvent;
import com.whoami.launch.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServiceEventConsumer {

	private final NotificationService notificationService;

	@KafkaListener(topics = KafkaTopics.SERVICE_EVENTS, groupId = "notification-group")
	public void consume(Object event) {

		if (event instanceof ServiceCreatedEvent e) {
			notificationService.handleServiceCreated(e);
		}

		else if (event instanceof ServiceUpdatedEvent e) {
			notificationService.handleServiceUpdated(e);
		}

		else if (event instanceof ServiceDeletedEvent e) {
			notificationService.handleServiceDeleted(e);
		}
	}
}
