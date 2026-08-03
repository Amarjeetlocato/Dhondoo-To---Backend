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
public class ReelCreatedEvent {

	private String eventId;
	private String eventType;
	private LocalDateTime eventTime;

	private String reelId;
	private String reelThumbnail;

	private String shopId;
	private String shopName;
	private String shopLogo;
	private String shopBanner;
	private String reelVideo;
	private String description;
	private String userId;
}