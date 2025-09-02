package com.imchobo.lease.domain;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {
  private Long dno;
  private Long mno;
  private Long pno;
  private Long addrno;
  private String trackingNo;
  private String carrierCode;
  private String memo;
  private DeliveryStatus status;
  private LocalDateTime shippedAt;
  private
  LocalDateTime regdate;
}
