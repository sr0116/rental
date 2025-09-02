package com.imchobo.lease.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryCreateRequest {
  private Long orderId;
  private Long subscribeId;
  private Long mno;
  private Long addrno;
  private Long pno;
  private String memo;
  private String carrierCode;  // 선택
}