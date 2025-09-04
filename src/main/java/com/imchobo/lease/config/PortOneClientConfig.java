package com.imchobo.lease.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * PortOneClientConfig
 * - application.yml에 정의된 포트원 API 키/시크릿/상점코드 값을 로드하는 설정 클래스
 * - ServiceImpl에서 이 설정을 주입받아 API 호출 시 사용
 */
@Configuration
@Getter
public class PortOneClientConfig {

  /**
   * PortOne REST API Key
   * - application.yml의 portone.api-key 값을 주입받음
   */
  @Value("${portone.api-key}")
  private String apiKey;

  /**
   * PortOne REST API Secret
   * - application.yml의 portone.api-secret 값을 주입받음
   */
  @Value("${portone.api-secret}")
  private String apiSecret;

  /**
   * 상점 코드 (merchant code, MID)
   * - PortOne에서 발급받은 PG사 상점 아이디
   * - 프론트 requestPay() init 할 때도 사용
   */
  @Value("${portone.merchant-code}")
  private String merchantCode;
}