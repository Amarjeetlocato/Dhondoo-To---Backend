package com.whoami.launch.order.delivery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeliveryGatewayFactory {

    private final List<DeliveryGatewayClient> clients;

    public DeliveryGatewayClient getGateway(String partnerName) {
        if (partnerName == null || partnerName.isBlank()) {
            return getDefaultGateway();
        }

        String normalizedName = partnerName.toUpperCase();
        if (normalizedName.startsWith("UBER")) {
            normalizedName = "UBER";
        }

        final String targetName = normalizedName;

        return clients.stream()
                .filter(client -> client.getPartnerName().equalsIgnoreCase(targetName))
                .findFirst()
                .orElseGet(this::getDefaultGateway);
    }

    private DeliveryGatewayClient getDefaultGateway() {
        return clients.stream()
                .filter(client -> client.getPartnerName().equalsIgnoreCase("CUSTOM_WEBSITE"))
                .findFirst()
                .orElse(clients.get(0));
    }
}
