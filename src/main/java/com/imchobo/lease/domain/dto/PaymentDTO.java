package com.imchobo.lease.domain.dto;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 결제 응답 DTO
 * - 클라이언트로 전달할 정보
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

  private Long paymentId;

  private Long orderId;

  private String merchantUid;

  private String impUid;

  /** 결제 금액 */
  private BigDecimal amount;

  private String paystatus;

  private String receipt;

  private LocalDateTime regdate;

  private LocalDateTime voiddate;
}
