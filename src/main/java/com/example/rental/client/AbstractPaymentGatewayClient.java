package com.example.rental.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public abstract class AbstractPaymentGatewayClient implements PaymentGatewayClient {

  private final String apiKey;
  private final String apiSecret;

  private final RestClient rest = RestClient.builder()
          .baseUrl("https://api.iamport.kr")
          .build();

  protected AbstractPaymentGatewayClient(String apiKey, String apiSecret) {
    this.apiKey = apiKey;
    this.apiSecret = apiSecret;
  }

  protected String token() {
    JsonNode j = rest.post()
            .uri("/users/getToken")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"imp_key\":\"" + apiKey + "\",\"imp_secret\":\"" + apiSecret + "\"}")
            .retrieve()
            .body(JsonNode.class);
    return j.get("response").get("access_token").asText();
  }

  @Override
  public boolean prepare(String merchantUid, int amount) {
    String token = token();
    JsonNode j = rest.post().uri("/payments/prepare")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", token)
            .body("{\"merchant_uid\":\"" + merchantUid + "\",\"amount\":" + amount + "}")
            .retrieve()
            .body(JsonNode.class);
    return j != null && j.get("code").asInt() == 0;
  }

  @Override
  public JsonNode verify(String impUid) {
    String token = token();
    JsonNode j = rest.get().uri("/payments/" + impUid)
            .header("Authorization", token)
            .retrieve()
            .body(JsonNode.class);
    return (j != null && j.get("code").asInt() == 0) ? j.get("response") : null;
  }

  @Override
  public boolean charge(String customerUid, int amount) {
    String token = token();
    String merchantUid = "order_" + System.currentTimeMillis();
    String body = "{"
            + "\"customer_uid\":\"" + customerUid + "\","
            + "\"merchant_uid\":\"" + merchantUid + "\","
            + "\"amount\":" + amount
            + "}";
    JsonNode j = rest.post().uri("/subscribe/payments/again")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", token)
            .body(body)
            .retrieve()
            .body(JsonNode.class);
    return j != null && j.get("code").asInt() == 0;
  }

  @Override
  public boolean refund(String impUid, int refundAmount, String reason) {
    String token = token();
    String body = "{"
            + "\"imp_uid\":\"" + impUid + "\","
            + "\"amount\":" + refundAmount + ","
            + "\"reason\":\"" + (reason != null ? reason : "refund") + "\""
            + "}";
    JsonNode j = rest.post().uri("/payments/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", token)
            .body(body)
            .retrieve()
            .body(JsonNode.class);
    return j != null && j.get("code").asInt() == 0;
  }
}
