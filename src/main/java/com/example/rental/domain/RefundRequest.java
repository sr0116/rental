package com.example.rental.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

// 4) 환불 요청
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequest {
  @NotBlank
  private String merchantUid;
  private Integer amount; // null이면 전액 환불
  private String reason;
}
