package com.example.rental.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentGatewayClientFactory {

  private final GeneralPaymentGatewayClient generalClient;
  private final RecurringPaymentGatewayClient recurringClient;

  public PaymentGatewayClient getClient(boolean recurring) {
    return recurring ? recurringClient : generalClient;
  }
}
