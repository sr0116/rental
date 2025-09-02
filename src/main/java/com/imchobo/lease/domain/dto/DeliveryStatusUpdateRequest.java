package com.imchobo.lease.domain.dto;


import lombok.*;
import com.imchobo.lease.domain.DeliveryStatus;

/**
 * 배송 상태 변경 요청 DTO
 * - 클라이언트에서 { "status": "SHIPPING" } 같은 JSON이 넘어올 때 매핑
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryStatusUpdateRequest {
  private DeliveryStatus status;
}
