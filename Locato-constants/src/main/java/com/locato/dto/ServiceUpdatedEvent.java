package com.locato.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceUpdatedEvent {

	private String eventId;
	private String eventType;
	private LocalDateTime eventTime;

	private String shopId;
	private String userId;

	private String shopName;
	private String logoUrl;
	private String bannerUrl;
	
	private Double price;
	
	private String serviceDescription;

	private String serviceId;
	private String serviceName;
	private String serviceThumbnail;


	private String changes;
}