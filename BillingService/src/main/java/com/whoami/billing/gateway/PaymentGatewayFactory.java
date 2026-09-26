package com.whoami.billing.gateway;

import com.whoami.billing.domain.entity.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentGatewayFactory {

    private final List<PaymentGatewayClient> gatewayClients;

    public PaymentGatewayClient getClient(
            PaymentGateway gateway) {

        Map<PaymentGateway, PaymentGatewayClient> clients =
                gatewayClients.stream()
                        .collect(Collectors.toMap(
                                PaymentGatewayClient::getGateway,
                                Function.identity()
                        ));

        PaymentGatewayClient client = clients.get(gateway);

        if (client == null) {
            throw new IllegalArgumentException(
                    "Unsupported payment gateway: " + gateway
            );
        }

        return client;
    }
}