package com.imchobo.lease.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
  private Long orderId;
  private Long memberId;
  private Long addrId;
  private String status; // PENDING / PAID / SHIPPED / DELIVERED / CANCELED
}