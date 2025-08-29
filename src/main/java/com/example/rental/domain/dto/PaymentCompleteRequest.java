package com.example.rental.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

// 2) 결제 완료 검증
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompleteRequest {
  @NotBlank private String impUid;
  @NotBlank private String merchantUid;
}