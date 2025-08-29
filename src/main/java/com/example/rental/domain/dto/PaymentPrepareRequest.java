package com.example.rental.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 1) 금액 사전등록/주문 준비
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPrepareRequest {
  @NotBlank
  private String merchantUid;
  @Positive
  private Integer amount;
  private String planType; // ONE_TIME or RECURRING
  private String userEmail;
  private String itemId;
  private String openId;
  private String customerUid; // 정기결제 시 지정
}