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
public class ServiceDeletedEvent {

	private String eventId;
	private String eventType;
	private LocalDateTime eventTime;

	private String serviceId;
	private String serviceName;
	private String serviceThumbnail;

	private String shopId;
	private String shopName;
	private String shopLogo;
	private String shopBanner;

	private String userId;
}