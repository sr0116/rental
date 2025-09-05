package com.imchobo.lease;

import com.imchobo.lease.config.PortOneClientConfig;
import com.imchobo.lease.domain.dto.TokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PortOneClient {

  private final PortOneClientConfig config;
  private final RestTemplate restTemplate = new RestTemplate();

  /** 액세스 토큰 발급 */
  public String getAccessToken() {
    String url = "https://api.iamport.kr/users/getToken";

    // DTO로 body 생성
    TokenRequest body = new TokenRequest(config.getApiKey(), config.getApiSecret());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<TokenRequest> entity = new HttpEntity<>(body, headers);
    ResponseEntity<Map> res = restTemplate.postForEntity(url, entity, Map.class);
    log.info("👉 PortOne 토큰 요청 DTO: {}", body);

    headers.setContentType(MediaType.APPLICATION_JSON);


    if (res.getBody() == null || res.getBody().get("response") == null) {
      throw new RuntimeException("PortOne 토큰 발급 실패: " + res);
    }

    Map response = (Map) res.getBody().get("response");
    return (String) response.get("access_token");
  }

  /** 환불 요청 */
  public Map cancelPayment(String impUid, Integer amount, String reason) {
    String token = getAccessToken();
    String url = "https://api.iamport.kr/payments/cancel";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(token);

    Map<String, Object> body = new HashMap<>();
    body.put("imp_uid", impUid);
    body.put("reason", reason);
    if (amount != null && amount > 0) {
      body.put("amount", amount);
    }

    log.info("👉 PortOne 환불 요청 body: {}", body);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
    ResponseEntity<Map> res = restTemplate.postForEntity(url, entity, Map.class);

    log.info("PortOne 환불 응답: {}", res.getBody());
    return res.getBody();
  }
}

