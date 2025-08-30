package com.example.rental.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * 정기 결제 재청구 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringChargeRequest {

  @NotBlank
  private String customerUid;   // PortOne에서 발급한 customer_uid (빌링키)

  @Positive
  private Integer amount;       // 청구 금액

  private String name;          // 결제 항목 이름 (optional)
  private String userEmail;     // 사용자 이메일
  private String itemId;        // 상품 ID (optional)
  private Integer periodDays;   // 결제 주기 (예: 30일) -> 기본값 30일
}
