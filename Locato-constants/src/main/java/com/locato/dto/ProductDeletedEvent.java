package com.locato.dto;

import java.time.LocalDateTime;
import java.util.List;

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
public class ProductDeletedEvent {

	private String eventId;
	private String eventType;
	private LocalDateTime eventTime;

	private String productId;
	private String productName;
	 private List<String> productImages;
	private String shopId;
	private String shopName;
	private String shopLogo;
	private String shopBanner;

	private String userId;
}