package com.imchobo.lease.domain.dto;

import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeDTO {
  private Long subscribeId;
  private Long orderId;
  private Long memberId;
  private String status;           // WAITING / ACTIVE / CANCELED / ENDED
  private Integer depositSnapshot; // 보증금 스냅샷
  private Integer monthlyFeeSnapshot; // 월 요금 스냅샷
}