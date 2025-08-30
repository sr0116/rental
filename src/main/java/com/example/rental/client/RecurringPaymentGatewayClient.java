package com.example.rental.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RecurringPaymentGatewayClient extends AbstractPaymentGatewayClient {

  public RecurringPaymentGatewayClient(
          @Value("${portone.recurring.api-key}") String apiKey,
          @Value("${portone.recurring.api-secret}") String apiSecret
  ) {
    super(apiKey, apiSecret);
  }
}
