package com.example.rental.client;

import com.fasterxml.jackson.databind.JsonNode;

public interface PaymentGatewayClient {
  boolean prepare(String merchantUid, int amount);
  JsonNode verify(String impUid);
  boolean charge(String customerUid, int amount);
  boolean refund(String impUid, int refundAmount, String reason);
}
