package com.imchobo.lease.domain.dto;


import lombok.*;

import java.math.BigDecimal;

/**
 * 환불 요청 DTO
 * - 만료 시 전액 환불 / 조기 해지 시 일부 환불(위약금 차감) 요청
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDTO {

  private Long paymentId;

  /** 환불 금액 */
  private BigDecimal amount;

  private String reason;
}

