package com.whoami.launch.dto;

import com.whoami.launch.enums.ShopStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopStatusChangedEvent {
	
	private String shopId;
	private String userId;
	private String shopName;
	private ShopStatus status;
	

}
