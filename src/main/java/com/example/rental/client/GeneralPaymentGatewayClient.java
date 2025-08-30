package com.example.rental.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeneralPaymentGatewayClient extends AbstractPaymentGatewayClient {

  public GeneralPaymentGatewayClient(
          @Value("${portone.general.api-key}") String apiKey,
          @Value("${portone.general.api-secret}") String apiSecret
  ) {
    super(apiKey, apiSecret);
  }
}
